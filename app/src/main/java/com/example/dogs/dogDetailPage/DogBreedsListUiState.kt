package com.example.dogs.dogDetailPage

data class DogBreedsListUiState(
    val isInitialLoading: Boolean = true,
    val screenError: String? = null,
    val isEmptyState: Boolean = false,
)
