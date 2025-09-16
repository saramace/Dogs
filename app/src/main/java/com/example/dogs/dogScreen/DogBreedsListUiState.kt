package com.example.dogs.dogScreen

data class DogBreedsListUiState(
    val isInitialLoading: Boolean = true,
    val screenError: String? = null,
    val isEmptyState: Boolean = false,
)
