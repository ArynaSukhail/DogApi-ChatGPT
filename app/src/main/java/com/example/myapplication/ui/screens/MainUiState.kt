package com.example.myapplication.ui.screens

import com.example.myapplication.domain.Breed

data class MainUiState(
    val breeds: List<Breed> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)