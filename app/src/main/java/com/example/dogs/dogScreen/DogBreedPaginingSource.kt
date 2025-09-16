package com.example.dogs.dogScreen

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.dogs.api.DogApi
import com.example.dogs.api.DogLists
import java.io.IOException

private const val DOG_API_STARTING_PAGE_INDEX = 1
const val DEFAULT_PAGE_SIZE = 10
class DogBreedPaginingSource(
    private val dogApi: DogApi
) : PagingSource<Int, DogLists>(){
    override fun getRefreshKey(state: PagingState<Int, DogLists>): Int? {

        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DogLists> {
        // If page is null, it's the initial load, so use the starting page index
        val pageNumber = params.key ?: DOG_API_STARTING_PAGE_INDEX
        val pageSize = params.loadSize.coerceAtMost(DEFAULT_PAGE_SIZE)

        return try {
            val response = dogApi.getDogBreedList(pageNumber = pageNumber, pageSize = pageSize)
            val breeds = response.data
            val meta = response.meta


            // Determine nextKey: if current page is last page, nextKey is null, else current page + 1
            val nextKey = if (meta.pagination.current == meta.pagination.records || breeds.isEmpty()) {
                null
            } else {
                pageNumber + 1
            }

            // Determine prevKey
            val prevKey = if (pageNumber == DOG_API_STARTING_PAGE_INDEX) {
                null // Only paging forward in this case, since its the first page
            } else {
                pageNumber - 1
            }


            LoadResult.Page(
                data = breeds,
                prevKey = prevKey,
                nextKey = nextKey
            )

    } catch (e: IOException) {
        LoadResult.Error(e)
    } catch (e: HttpException) {
        LoadResult.Error(e)
    }
}


}

