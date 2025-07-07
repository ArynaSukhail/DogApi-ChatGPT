package com.example.myapplication

import com.example.myapplication.data.BreedDao
import com.example.myapplication.data.BreedDto
import com.example.myapplication.data.BreedEntity
import com.example.myapplication.data.DogApi
import com.example.myapplication.data.DogRepositoryImpl
import com.example.myapplication.data.Image
import com.example.myapplication.data.Measure
import com.example.myapplication.data.NetworkChecker
import com.example.myapplication.domain.Breed
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DogRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var api: DogApi
    private lateinit var breedDao: BreedDao
    private lateinit var networkChecker: NetworkChecker
    private lateinit var repository: DogRepositoryImpl

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        api = mockk()
        breedDao = mockk()
        networkChecker = mockk()
        repository = DogRepositoryImpl(api, breedDao, networkChecker)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when internet available, fetch from network and update cache`() = runTest {
        // Мокаем, что интернет доступен
        every { networkChecker.isInternetAvailable() } returns true

        val remoteBreeds = listOf(
            Breed(1, "Breed1", "Calm", "10-15", "30-40", "url1"),
            Breed(2, "Breed2", "Active", "20-25", "50-60", "url2")
        )

        coEvery { api.getBreeds() } returns remoteBreeds.map { it.toDto() }
        coEvery { breedDao.insertAll(any()) } just Runs

        val result = repository.getBreeds()

        coVerify { api.getBreeds() }
        coVerify { breedDao.insertAll(any()) }
        assertEquals(remoteBreeds.size, result.size)
        assertEquals(remoteBreeds.first().name, result.first().name)
    }

    @Test
    fun `when internet available but api throws, load from cache`() = runTest {
        every { networkChecker.isInternetAvailable() } returns true

        coEvery { api.getBreeds() } throws RuntimeException("API error")

        val cachedBreeds = listOf(
            Breed(3, "CachedBreed", "Friendly", "15-20", "40-50", "url3")
        )

        coEvery { breedDao.getAll() } returns cachedBreeds.map { it.toEntity() }

        val result = repository.getBreeds()

        coVerify { breedDao.getAll() }
        assertEquals(cachedBreeds.size, result.size)
        assertEquals(cachedBreeds.first().name, result.first().name)
    }

    @Test
    fun `when no internet, load from cache`() = runTest {
        every { networkChecker.isInternetAvailable() } returns false

        val cachedBreeds = listOf(
            Breed(4, "CachedBreed2", "Calm", "10-15", "30-40", "url4")
        )

        coEvery { breedDao.getAll() } returns cachedBreeds.map { it.toEntity() }

        val result = repository.getBreeds()

        coVerify(exactly = 0) { api.getBreeds() }
        coVerify { breedDao.getAll() }
        assertEquals(cachedBreeds.size, result.size)
        assertEquals(cachedBreeds.first().name, result.first().name)
    }
}

// Для удобства нужен маппер из domain Breed в DTO и Entity, если их нет — реализуй, например:

fun Breed.toDto(): BreedDto {
    // Пример — нужно по структуре DTO
    return BreedDto(
        id = id,
        name = name,
        temperament = temperament,
        weight = Measure(metric = weight),
        height = Measure(metric = height),
        image = Image(url = imageUrl)
    )
}

fun Breed.toEntity(): BreedEntity {
    // Пример — нужно по структуре Entity
    return BreedEntity(
        id = id,
        name = name,
        temperament = temperament,
        weight = weight,
        height = height,
        imageUrl = imageUrl
    )
}
