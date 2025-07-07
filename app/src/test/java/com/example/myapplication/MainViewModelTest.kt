import com.example.myapplication.domain.Breed
import com.example.myapplication.domain.DogRepository
import com.example.myapplication.ui.screens.MainViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: DogRepository
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = MainViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadNextPage success updates state with breeds`() = runTest {
        val breeds = listOf(
            Breed(1, "Breed1", "Calm", "10-15", "30-40", "url1"),
            Breed(2, "Breed2", "Active", "20-25", "50-60", "url2")
        )
        coEvery { repository.getBreeds(0, 10) } returns breeds

        viewModel.loadNextPage()

        // Прокручиваем dispatcher, чтобы завершились корутины
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(breeds, state.breeds)
        assertTrue(state.error == null)
        assertTrue(state.isLoading == false)
    }

    @Test
    fun `loadNextPage failure updates state with error`() = runTest {
        val errorMessage = "Network error"
        coEvery { repository.getBreeds(0, 10) } throws RuntimeException(errorMessage)

        viewModel.loadNextPage()

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.breeds.isEmpty())
        assertEquals(errorMessage, state.error)
        assertTrue(state.isLoading == false)
    }

    @Test
    fun `loadNextPage prevents concurrent loading`() = runTest {
        val breeds = listOf(Breed(1, "Breed1", "Calm", "10-15", "30-40", "url1"))
        coEvery { repository.getBreeds(0, 10) } returns breeds

        // Запускаем загрузку первой страницы
        viewModel.loadNextPage()

        // Сразу пытаемся загрузить еще одну (конкурентная)
        viewModel.loadNextPage()

        testDispatcher.scheduler.advanceUntilIdle()

        // Проверяем что данные не дублируются, т.к. вторая загрузка игнорится
        val state = viewModel.uiState.value
        assertEquals(1, state.breeds.size)
    }
}
