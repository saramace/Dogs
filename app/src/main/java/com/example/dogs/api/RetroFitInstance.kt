package com.example.dogs.api

import okhttp3.OkHttpClient
import kotlin.getValue
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroFitInstance {

    private const val BASE_URL = "https://dogapi.dog/api/v2/"

    val api: DogApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DogApi::class.java)
    }
}