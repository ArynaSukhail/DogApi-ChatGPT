package com.example.myapplication.data

import com.example.myapplication.domain.Breed
import com.example.myapplication.domain.DogRepository
import javax.inject.Inject

class DogRepositoryImpl @Inject constructor(
    private val api: DogApi,
    private val breedDao: BreedDao,
    private val networkChecker: NetworkChecker
) : DogRepository {

    override suspend fun getBreeds(offset: Int, limit: Int): List<Breed> {
        return if (networkChecker.isInternetAvailable()) {
            try {
                val remoteBreeds = api.getBreeds().map { it.toDomain() }
                // Обновляем кеш целиком (или можно реализовать дифференцированное обновление)
                breedDao.insertAll(remoteBreeds.map { it.toEntity() })
                // Возвращаем нужную страницу из всего списка
                remoteBreeds.drop(offset).take(limit)
            } catch (e: Exception) {
                breedDao.getAll().map { it.toDomain() }.drop(offset).take(limit)
            }
        } else {
            breedDao.getAll().map { it.toDomain() }.drop(offset).take(limit)
        }
    }
}

