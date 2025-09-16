package com.example.dogs.ui.viewModels

import androidx.compose.animation.core.copy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.dogs.api.DogLists
import com.example.dogs.api.DogRepository
import com.example.dogs.dogDetailPage.DogBreedsListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class DogListViewModel @Inject constructor(
    repository : DogRepository
) : ViewModel() {

    val dogBreedsPager: Flow<PagingData<DogLists>> = repository.getDogBreedsPager()
        .cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(DogBreedsListUiState())
    val uiState: StateFlow<DogBreedsListUiState> = _uiState.asStateFlow()



    fun updateEmptyState(isEmpty: Boolean) {
        _uiState.update { it.copy(isEmptyState = isEmpty, isInitialLoading = false, screenError = null) }
    }

    fun setLoadingInitial(isLoading: Boolean) {
        _uiState.update { it.copy(isInitialLoading = isLoading, isEmptyState = if (isLoading) false else it.isEmptyState) }
    }


}
