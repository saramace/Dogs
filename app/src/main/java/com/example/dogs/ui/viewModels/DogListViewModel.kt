package com.example.dogs.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.dogs.api.DogLists
import com.example.dogs.api.DogRepository
import com.example.dogs.api.RetroFitInstance
import kotlinx.coroutines.flow.Flow


class DogListViewModel(
    repository: DogRepository = DogRepository(RetroFitInstance.api),

) : ViewModel() {

    val dogBreedsPager: Flow<PagingData<DogLists>> = repository.getDogBreedsPager()
        .cachedIn(viewModelScope)

}
