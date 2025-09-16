package com.example.dogs.api

import kotlinx.serialization.Serializable

@Serializable
data class DogListResponse(
    val data: List<DogLists>,
    val meta: Meta,
    val links: Links,
)


@Serializable
data class DogLists(
    val id: String,
    val type: String,
    val attributes: BreedInfo,
)

@Serializable
data class BreedInfo(
    val name: String,
    val description: String,
    val hypoallergenic: Boolean,
    val life: Life,
    val maleWeight: Weight? = null,
    val femaleWeight: Weight? = null,
)

@Serializable
data class Life(
    val min: Long,
    val max: Long,
)

@Serializable
data class Weight(
    val min: Long,
    val max: Long,
)

@Serializable
data class Meta(
    val pagination: Pagination,
)

@Serializable
data class Pagination(
    val current: Long,
    val records: Long,
)

@Serializable
data class Links(
    val self: String,
    val current: String,
    val next: String,
    val last: String,
)
