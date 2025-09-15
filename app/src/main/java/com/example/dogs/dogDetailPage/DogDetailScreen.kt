package com.example.dogs.dogDetailPage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dogs.api.BreedInfo
import com.example.dogs.ui.viewModels.DogDetailsViewModel
import com.example.dogs.ui.viewModels.DogDetailsViewModelFactory

@Composable
fun DogDetailsScreen(dogId: String, navController: NavController, modifier: Modifier) {

    val viewModel: DogDetailsViewModel = viewModel(
        factory = DogDetailsViewModelFactory(dogId)
    )
    val dogDetails: BreedInfo? by viewModel.dogDetails.observeAsState(initial = null)


    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (dogDetails == null && dogId.isNotBlank()) { // Show loading only if dogId is valid and details are null
           CircularProgressIndicator()
        } else if (dogDetails != null) {
            // Data has loaded, display it
            val details = dogDetails!! // Safe non-null assertion because of the check
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
        else {
            // Error state or dogId was invalid initially
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Could not load dog details for ID: $dogId")
                Button(onClick = { navController.popBackStack() }, modifier = Modifier.padding(top = 16.dp)) {
                    Text("Back")
                }
            }
        }
    }
}

