package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import com.example.myapplication.domain.Breed
import com.example.myapplication.ui.components.BreedCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedListScreen(
    state: MainUiState,
    onItemClick: (Breed) -> Unit,
    onLoadMore: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Породы собак",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        BreedListContent(
            state = state,
            onItemClick = onItemClick,
            modifier = Modifier.padding(paddingValues),
            onLoadMore = onLoadMore
        )
    }
}

@Composable
fun BreedListContent(
    state: MainUiState,
    onItemClick: (Breed) -> Unit,
    modifier: Modifier = Modifier,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            state.isLoading && state.breeds.isEmpty() -> {
                LoadingIndicator()
            }

            state.error != null && state.breeds.isEmpty() -> {
                ErrorText(message = state.error)
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().testTag("BreedListLazyColumn"),
                    state = listState
                ) {
                    items(state.breeds) { breed ->
                        BreedCard(
                            breed = breed,
                            onClick = onItemClick,
                        )
                    }

                    // Показать индикатор загрузки внизу, если идет подгрузка
                    if (state.isLoading && state.breeds.isNotEmpty()) {
                        item {
                            LoadingIndicator()
                        }
                    }
                }

                // Подгрузка при достижении конца списка
                LaunchedEffect(listState) {
                    snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                        .collect { lastVisibleItemIndex ->
                            val totalItemsCount = listState.layoutInfo.totalItemsCount
                            if (lastVisibleItemIndex != null && lastVisibleItemIndex >= totalItemsCount - 3) {
                                onLoadMore()
                            }
                        }
                }
            }
        }
    }
}


@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize().testTag("LoadingIndicator"),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorText(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Ошибка: $message",
            color = MaterialTheme.colorScheme.error
        )
    }
}


