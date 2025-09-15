package com.example.dogs.ui


object NavDestinations {
    const val DOG_BREEDS_LIST = "dogBreedsList"
    const val DOG_DETAIL = "dogDetail"
    const val DOG_ID_ARG = "dogId" // Argument key
}

// Define the route for DogDetail screen, including the argument placeholder
val dogDetailRoute = "${NavDestinations.DOG_DETAIL}/{${NavDestinations.DOG_ID_ARG}}"

// Helper function to create the navigation path with an actual ID
fun navigateToDogDetail(dogId: String): String {
    return "${NavDestinations.DOG_DETAIL}/$dogId"
}
