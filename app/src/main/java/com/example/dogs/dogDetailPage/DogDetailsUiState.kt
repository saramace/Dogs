package com.example.dogs.dogDetailPage

import com.example.dogs.api.BreedInfo


data class DogDetailsUiState(
    val isLoading: Boolean = false,
    val dogDetails: BreedInfo? = null,
    val errorMessage: String? = null
)