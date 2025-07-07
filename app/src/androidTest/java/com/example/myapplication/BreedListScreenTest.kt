package com.example.myapplication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToIndex
import com.example.myapplication.domain.Breed
import com.example.myapplication.ui.screens.BreedListScreen
import com.example.myapplication.ui.screens.MainUiState
import org.junit.Rule
import org.junit.Test

class BreedListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleBreeds = listOf(
        Breed(1, "Bulldog", "Calm", "20-25", "30-35", "url1"),
        Breed(2, "Beagle", "Active", "15-20", "25-30", "url2"),
        Breed(3, "Poodle", "Smart", "10-15", "20-25", "url3")
    )

    @Test
    fun loading_showsProgressIndicator() {
        val state = MainUiState(isLoading = true, breeds = emptyList())

        composeTestRule.setContent {
            BreedListScreen(
                state = state,
                onItemClick = {},
                onLoadMore = {}
            )
        }

        composeTestRule.onNode(hasTestTag("LoadingIndicator")).assertIsDisplayed()
    }

    @Test
    fun error_showsErrorMessage() {
        val state = MainUiState(error = "Network Error", breeds = emptyList())

        composeTestRule.setContent {
            BreedListScreen(
                state = state,
                onItemClick = {},
                onLoadMore = {}
            )
        }

        composeTestRule.onNodeWithText("Ошибка: Network Error").assertIsDisplayed()
    }

    @Test
    fun breeds_showInList() {
        val state = MainUiState(breeds = sampleBreeds)

        composeTestRule.setContent {
            BreedListScreen(
                state = state,
                onItemClick = {},
                onLoadMore = {}
            )
        }

        sampleBreeds.forEach {
            composeTestRule.onNodeWithText(it.name).assertIsDisplayed()
        }
    }

    @Test
    fun onLoadMore_called_whenScrolledToEnd() {
        var loadMoreCalled = false

        val breeds = (1..20).map { index ->
            Breed(
                id = index,
                name = "Breed $index",
                temperament = "Friendly",
                weight = "10",
                height = "50",
                imageUrl = ""
            )
        }

        composeTestRule.setContent {
            BreedListScreen(
                state = MainUiState(breeds = breeds, isLoading = false),
                onItemClick = {},
                onLoadMore = {
                    loadMoreCalled = true
                }
            )
        }

        // Используем performScrollToIndex для прокрутки к последнему элементу
        composeTestRule.onNodeWithTag("BreedListLazyColumn")
            .performScrollToIndex(breeds.size - 1)

        // Проверяем что элемент виден
        composeTestRule.onNodeWithText("Breed 20")
            .assertIsDisplayed()

        // Дополнительно, если onLoadMore вызывается при достижении конца, можно проверить вызов:
        // В данном случае переменная loadMoreCalled должна стать true (требуется механизм ожидания, см ниже)
        composeTestRule.runOnIdle {
            assert(loadMoreCalled)
        }
    }

}
