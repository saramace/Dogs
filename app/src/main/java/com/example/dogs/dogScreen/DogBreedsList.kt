package com.example.dogs.dogScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.dogs.api.BreedInfo
import com.example.dogs.api.DogLists
import com.example.dogs.api.Life
import com.example.dogs.api.Weight
import com.example.dogs.ui.navigateToDogDetail
import com.example.dogs.ui.theme.DogsTheme
import com.example.dogs.ui.viewModels.DogListViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map


@Composable
fun DogBreedsList(
    navController: NavController,
    dogListViewModel: DogListViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val lazyDogBreeds: LazyPagingItems<DogLists> =
        dogListViewModel.dogBreedsPager.collectAsLazyPagingItems()

    val screenUiState by dogListViewModel.uiState.collectAsState()

    LaunchedEffect(lazyDogBreeds.loadState) {
        // Create a flow for the refresh load state
        snapshotFlow { lazyDogBreeds.loadState.refresh }
            .distinctUntilChanged()
            .filter { it is LoadState.NotLoading } // Only when refresh is NotLoading
            .map { lazyDogBreeds.itemCount == 0 }
            .distinctUntilChanged() // Only update if the emptiness state changes
            .collect { isEmpty ->
                if (screenUiState.screenError == null && !screenUiState.isInitialLoading) {
                    dogListViewModel.updateEmptyState(isEmpty)
                }
            }
    }

    LaunchedEffect(lazyDogBreeds.loadState.refresh) {
        val refreshState = lazyDogBreeds.loadState.refresh
        if (refreshState is LoadState.Loading && lazyDogBreeds.itemCount == 0) {
            dogListViewModel.setLoadingInitial(true)
        } else if (refreshState !is LoadState.Loading) {
            dogListViewModel.setLoadingInitial(false)
        }
    }

    when {
        screenUiState.isInitialLoading && lazyDogBreeds.itemCount == 0 -> {
            // Showing full screen loader only if list is empty
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        screenUiState.screenError != null -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error: ${screenUiState.screenError}")
                    Button(onClick = { lazyDogBreeds.retry() }) { // this is for Paging's retry
                        Text("Retry")
                    }
                }
            }
        }

        screenUiState.isEmptyState -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No dog breeds found.")
            }
        }

        else -> {

            LazyColumn(modifier = modifier) {
                items(
                    count = lazyDogBreeds.itemCount,
                    key = lazyDogBreeds.itemKey { it.id }
                ) { item ->

                    val backgroundColor = if (item % 2 == 0) Color.LightGray else Color.White

                    val dog = lazyDogBreeds[item]
                    if (dog != null) {
                        DogBreedCell(
                            dogList = dog,
                            modifier = Modifier.background(backgroundColor),
                            onDogClick = { dogId ->
                                navController.navigate(navigateToDogDetail(dogId))
                            }
                        )
                    } else {
                        //  for when item is null
                        Text("Dog breed is null")
                    }
                }
                lazyDogBreeds.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            }
                        }

                        loadState.append is LoadState.Loading -> {
                            item {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            }
                        }

                        loadState.refresh is LoadState.Error -> {
                            val e = loadState.refresh as LoadState.Error
                            item {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text("Error loading initial data: ${e.error.localizedMessage}")
                                    Button(onClick = { retry() }) { Text("Retry") }
                                }
                            }
                        }

                        loadState.append is LoadState.Error -> {
                            val e = loadState.append as LoadState.Error
                            item {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text("Error loading more data: ${e.error.localizedMessage}")
                                    Button(onClick = { retry() }) { Text("Retry") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun DogBreedCell(dogList: DogLists ,
                 modifier: Modifier,
                 onDogClick: (String) -> Unit
){
val dogName = dogList.attributes.name
    val dogId = dogList.id
    Text(
        text = dogName,
        fontSize = 30.sp,
        modifier = modifier
            .clickable(onClick = { onDogClick(dogId) }, role = Role.Button)
            .padding(16.dp)
            .fillMaxWidth()

    )
}

@Preview
@Composable
fun DogBreedCellPreview(){
    val mockDogData = DogLists(
        id = "1",
        type = "breed",
        attributes = BreedInfo(
            name = "Preview Doggo",
            description = "A very good dog for previews.",
            life = Life(min = 10, max = 12),
            maleWeight = Weight(min = 20, max = 25),
            femaleWeight = Weight(min = 18, max = 22),
            hypoallergenic = false
        )
    )
    DogsTheme {
        DogBreedCell(
            dogList = mockDogData,
            onDogClick = { dogId ->
                Log.d("PreviewClick", "Dog cell clicked with ID: $dogId")
            },
            modifier = Modifier
        )
    }

}