package com.example.myapplication.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.DogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: DogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    private var currentPage = 0
    private val pageSize = 10
    private var isLoadingMore = false
    private var allLoaded = false

    init {
        loadNextPage()
    }

    fun loadNextPage() {
        if (isLoadingMore || allLoaded) return

        isLoadingMore = true
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val newBreeds = repository.getBreeds(offset = currentPage * pageSize, limit = pageSize)
                allLoaded = newBreeds.size < pageSize

                val updatedList = _uiState.value.breeds + newBreeds

                _uiState.update {
                    it.copy(
                        breeds = updatedList,
                        isLoading = false,
                        error = null
                    )
                }
                currentPage++
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Произошла ошибка",
                        isLoading = false
                    )
                }
            }
            isLoadingMore = false
        }
    }
}
