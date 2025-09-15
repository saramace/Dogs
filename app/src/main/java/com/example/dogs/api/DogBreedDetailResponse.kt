package com.example.dogs.api

import kotlinx.serialization.Serializable

@Serializable
    data class DogBreedDetailResponse(
        val data: DogBreedData
    )

@Serializable
data class DogBreedData(
    val id: String,
    val type: String,
    val attributes: BreedInfo,
    val relationships: BreedRelationships? = null
)

@Serializable
data class BreedRelationships(
    val group: BreedGroupLink? = null,
)

@Serializable
data class BreedGroupLink(
    val data: BreedGroupData? = null
)

@Serializable
data class BreedGroupData(
    val id: String,
    val type: String
)