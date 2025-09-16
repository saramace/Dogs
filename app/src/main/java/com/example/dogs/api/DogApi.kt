package com.example.dogs.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DogApi {

    @GET("breeds")
    suspend fun getDogBreedList(
        @Query("page[number]") pageNumber: Int,
        @Query("page[size]") pageSize: Int
    ) : DogListResponse

   @GET("breeds/{id}")
    suspend fun getDogBreedDetailsById(@Path("id") id: String): DogBreedDetailResponse
}