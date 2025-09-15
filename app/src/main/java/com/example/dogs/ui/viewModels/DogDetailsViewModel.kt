package com.example.dogs.ui.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dogs.api.BreedInfo
import com.example.dogs.api.RetroFitInstance
import kotlinx.coroutines.launch

class DogDetailsViewModel(dogId: String): ViewModel() {

    private val _dogDetails = MutableLiveData<BreedInfo?>()
    val dogDetails: MutableLiveData<BreedInfo?> = _dogDetails

    init {
        loadData(dogid = dogId)
    }


    fun loadData(dogid: String) {
        viewModelScope.launch {
            try {
                val response = RetroFitInstance.api.getDogBreedDetailsById(dogid)
                val breedAttributes: BreedInfo = response.data.attributes
                _dogDetails.value = breedAttributes


                Log.d("DogViewModel", "Fetched dog: ${breedAttributes.name}")

         } catch (e: Exception) {
                Log.e("tag", "exception is " + e)

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