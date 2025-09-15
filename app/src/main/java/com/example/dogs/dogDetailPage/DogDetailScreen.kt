package com.example.dogs.dogDetailPage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.dogs.api.BreedInfo
import com.example.dogs.api.Life
import com.example.dogs.api.Weight
import com.example.dogs.ui.theme.DogsTheme
import com.example.dogs.ui.viewModels.DogDetailsViewModel
import com.example.dogs.ui.viewModels.DogDetailsViewModelFactory

@Composable
fun DogDetailsScreen(dogId: String, navController: NavController, modifier: Modifier) {

    val viewModel: DogDetailsViewModel = viewModel(
        factory = DogDetailsViewModelFactory(dogId)
    )

    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }

            uiState.errorMessage != null -> {
              val errorMessage = uiState.errorMessage
             DogDetailsErrorScreen(
                 viewModel = viewModel,
                 dogId = dogId,
                 errorMessage = errorMessage,
                 navController = navController,
                 modifier = modifier
             )

            }
            uiState.dogDetails != null -> {
                val details = uiState.dogDetails!! // Safe non-null assertion due to the when condition
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Details for: ${details.name}", style = MaterialTheme.typography.headlineSmall)
                    Text("Description: ${details.description ?: "N/A"}")
                    Text("Hypoallergenic: ${if (details.hypoallergenic) "Yes" else "No"}")
                    Text("Life Span: ${details.life.min} - ${details.life.max} years")

                    DogDetailsBackButton(navController = navController,
                        modifier = Modifier)
                }
            }
        }
    }


}


@Composable
fun DogDetailsErrorScreen(
    viewModel: DogDetailsViewModel,
    dogId: String,
    errorMessage: String?,
    navController: NavController,
    modifier: Modifier
) {
 Column(modifier = modifier) {
     Text("Error: ${errorMessage ?: "An unknown error occurred."}")
     DogDetailsRetryButton(viewModel, dogId, Modifier)
     DogDetailsBackButton(navController, Modifier)

 }
}
    @Composable
    fun DogDetailsRetryButton(viewModel: DogDetailsViewModel, dogId: String, modifier: Modifier){
        Button(
            onClick = { viewModel.loadDogDetails(dogId) }, // Example of calling ViewModel action
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Retry")
        }
    }

    @Composable
    fun DogDetailsBackButton( navController: NavController, modifier: Modifier){
        Button(onClick = { navController.popBackStack() }, modifier = modifier.padding(top = 16.dp)) {
            Text("Back")
        }
    }


@Preview
@Composable
fun DogDetailsScreenPreview_Success(){
    val mockDetails = BreedInfo(
        name = "Preview Doggo",
        description = "A very good and fluffy doggo, excellent for previews. Loves to chase virtual squirrels.",
        life = Life(min = 10, max = 14),
        maleWeight = Weight(min = 20, max = 25),
        femaleWeight = Weight(min = 18, max = 22),
        hypoallergenic = true
    )
    val mockUiState = DogDetailsUiState(isLoading = false, dogDetails = mockDetails, errorMessage = null)
    val navController = rememberNavController()

DogDetailsScreen(
    dogId = "1",
    navController = navController,
    modifier = Modifier
)
    PreviewDogDetailsContent(uiState = mockUiState, navController = navController)
}


//a helper composable to avoid mocking the viewModel
@Composable
fun PreviewDogDetailsContent(uiState: DogDetailsUiState, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }
            uiState.errorMessage != null -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Error: ${uiState.errorMessage}")
                    Button(onClick = { /* No-op for preview */ }, modifier = Modifier.padding(top = 16.dp)) {
                        Text("Retry")
                    }
                    Button(onClick = { navController.popBackStack() }, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Back")
                    }
                }
            }

            uiState.dogDetails != null -> {
                val details = uiState.dogDetails!!
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Details for: ${details.name}", style = MaterialTheme.typography.headlineSmall)
                    Text("Description: ${details.description ?: "N/A"}")
                    Text("Hypoallergenic: ${if (details.hypoallergenic) "Yes" else "No"}")
                    Text("Life Span: ${details.life.min} - ${details.life.max} years")
                    Button(onClick = { navController.popBackStack() }, modifier = Modifier.padding(top = 16.dp)) {
                        Text("Back")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DogDetailsScreenPreview_Loading() {
    val navController = rememberNavController()
    DogsTheme {
        Surface {
            // Using the helper with loading state
            PreviewDogDetailsContent(uiState = DogDetailsUiState(isLoading = true), navController = navController)
        }
    }
}


@Preview
@Composable
fun DogDetailsScreenPreview_Error() {
    val navController = rememberNavController()
    DogsTheme {
        Surface {
            // Using the helper with error state
            PreviewDogDetailsContent(
                uiState = DogDetailsUiState(errorMessage = "Failed to fetch dog details. Please check your network connection."),
                navController = navController
            )
        }
    }
}

