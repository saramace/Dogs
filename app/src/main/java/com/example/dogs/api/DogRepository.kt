package com.example.dogs.api

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.dogs.dogDetailPage.DEFAULT_PAGE_SIZE
import com.example.dogs.dogDetailPage.DogBreedPaginingSource
import kotlinx.coroutines.flow.Flow

class DogRepository(private val dogApi: DogApi)  {


    fun getDogBreedsPager(): Flow<PagingData<DogLists>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { DogBreedPaginingSource(dogApi) }
        ).flow
    }

}