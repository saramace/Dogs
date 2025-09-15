package com.example.dogs.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dogs.api.BreedInfo
import com.example.dogs.api.RetroFitInstance
import com.example.dogs.dogDetailPage.DogDetailsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DogDetailsViewModel(dogId: String): ViewModel() {

    private val _uiState = MutableStateFlow(DogDetailsUiState())
    val uiState: StateFlow<DogDetailsUiState> = _uiState.asStateFlow()


    init {
        if(dogId.isNotBlank())
        {
            loadDogDetails(dogid = dogId)

        }
        else {
            _uiState.update {
                it.copy(errorMessage = "Dog id is blank")
            }
        }
    }


    fun loadDogDetails(dogid: String) {
        //Setting ui state to loading and error message to null as there is currently no error message
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = RetroFitInstance.api.getDogBreedDetailsById(dogid)
                val breedAttributes: BreedInfo = response.data.attributes

                //setting uiState to not be loading and adding dog details
                _uiState.update {
                    it.copy(dogDetails = breedAttributes, isLoading = false)
                }

                Log.d("DogDetailsViewModel", "Fetched dog: ${breedAttributes.name}")

         } catch (e: Exception) {
                Log.e("DogDetailsViewModel", "exception is $e")

                //setting uiState to not be loading and adding error message
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Failed to fetch dog details: ${e.message}")
                }
            }
        }
    }

}


class DogDetailsViewModelFactory(private val dogId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DogDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DogDetailsViewModel(dogId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}