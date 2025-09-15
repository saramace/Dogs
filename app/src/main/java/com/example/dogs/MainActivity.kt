package com.example.dogs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dogs.dogDetailPage.DogBreedsList
import com.example.dogs.dogDetailPage.DogDetailsScreen
import com.example.dogs.ui.NavDestinations
import com.example.dogs.ui.dogDetailRoute
import com.example.dogs.ui.theme.DogsTheme
import com.example.dogs.ui.viewModels.DogListViewModel

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DogsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))

                }
            }
        }
    }
}


@Composable
fun AppNavigation(
    modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavDestinations.DOG_BREEDS_LIST
    ) {
        composable(NavDestinations.DOG_BREEDS_LIST) {
     DogBreedsList(
                navController = navController,
                 modifier = Modifier
            )
        }

        composable(
            route = dogDetailRoute,
            arguments = listOf(navArgument(NavDestinations.DOG_ID_ARG) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val dogId = backStackEntry.arguments?.getString(NavDestinations.DOG_ID_ARG)
            if (dogId != null) {
                DogDetailsScreen(dogId = dogId, navController = navController, modifier = modifier)
            } else {
                // Handle error
                Text("Error: Dog ID not found.")
            }
        }
    }
}



