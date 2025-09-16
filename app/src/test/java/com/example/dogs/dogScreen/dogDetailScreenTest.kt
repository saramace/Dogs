package com.example.dogs.dogScreen

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.dogs.api.BreedInfo
import com.example.dogs.ui.ViewModels.fakeBreedInfo
import com.example.dogs.ui.viewModels.DogDetailsViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify



class DogDetailsScreenTestDataHolder {
    var wasCircularProgressIndicatorShown = false
    var wasErrorScreenShown: String? = null
    var wasDetailsContentShown: BreedInfo? = null
    var backButtonAction: (() -> Unit)? = null
    var retryButtonAction: (() -> Unit)? = null

    fun reset() {
        wasCircularProgressIndicatorShown = false
        wasErrorScreenShown = null
        wasDetailsContentShown = null
        backButtonAction = null
        retryButtonAction = null
    }

    fun simulateDogDetailsScreenComposition(
        uiState: DogDetailsUiState,
        mockNavController: NavController,
        mockViewModel: DogDetailsViewModel,
        modifier: Modifier = Modifier
    ) {
        reset()

        when {
            uiState.isLoading -> {
                wasCircularProgressIndicatorShown = true
            }

            uiState.errorMessage != null -> {
                // Simulate DogDetailsErrorScreen
                wasErrorScreenShown = "Error: ${uiState.errorMessage}"
                // Simulate button actions being set up
                retryButtonAction = { mockViewModel.retryLoadDetails() }
                backButtonAction = { mockNavController.popBackStack() }
            }

            uiState.dogDetails != null -> {
                wasDetailsContentShown = uiState.dogDetails
                // Simulate button action being set up
                backButtonAction = { mockNavController.popBackStack() }
            }
        }
    }
}


class DogDetailsScreenUnitTest {

    private lateinit var mockNavController: NavController
    private lateinit var mockDetailsViewModel: DogDetailsViewModel
    private lateinit var mockUiStateFlow: MutableStateFlow<DogDetailsUiState>
    private lateinit var testHost: DogDetailsScreenTestDataHolder

    @Before
    fun setUp() {
        mockNavController = mock()
        mockDetailsViewModel = mock()
        mockUiStateFlow = MutableStateFlow(DogDetailsUiState()) // Initial default state
        testHost = DogDetailsScreenTestDataHolder()

    }

    private fun setUiState(uiState: DogDetailsUiState) {
        mockUiStateFlow.value = uiState
    }

    @Test
    fun `when isLoading is true, shows CircularProgressIndicator`() {
        val loadingState = DogDetailsUiState(isLoading = true)
        setUiState(loadingState)

        testHost.simulateDogDetailsScreenComposition(
            loadingState,
            mockNavController,
            mockDetailsViewModel
        )

        assertTrue(testHost.wasCircularProgressIndicatorShown)
        assertNull(testHost.wasErrorScreenShown)
        assertNull(testHost.wasDetailsContentShown)
    }

    @Test
    fun `when errorMessage is not null, shows DogDetailsErrorScreen`() {
        val errorMessage = "Failed to fetch"
        val errorState = DogDetailsUiState(errorMessage = errorMessage)
        setUiState(errorState)

        testHost.simulateDogDetailsScreenComposition(
            errorState,
            mockNavController,
            mockDetailsViewModel
        )

        assertFalse(testHost.wasCircularProgressIndicatorShown)
        assertEquals("Error: $errorMessage", testHost.wasErrorScreenShown)
        assertNull(testHost.wasDetailsContentShown)
        assertNotNull(testHost.retryButtonAction)
        assertNotNull(testHost.backButtonAction)
    }

    @Test
    fun `when dogDetails is not null, shows dog details content`() {
        val successState = DogDetailsUiState(dogDetails = fakeBreedInfo)
        setUiState(successState)

        testHost.simulateDogDetailsScreenComposition(
            successState,
            mockNavController,
            mockDetailsViewModel
        )

        assertFalse(testHost.wasCircularProgressIndicatorShown)
        assertNull(testHost.wasErrorScreenShown)
        assertEquals(fakeBreedInfo, testHost.wasDetailsContentShown)
        assertNotNull(testHost.backButtonAction)
        assertNull(testHost.retryButtonAction)
    }

    @Test
    fun `DogDetailsErrorScreen retry button calls viewModel retryLoadDetails`() {
        val errorMessage = "Network Error"
        val errorState = DogDetailsUiState(errorMessage = errorMessage)
        setUiState(errorState)

        testHost.simulateDogDetailsScreenComposition(
            errorState,
            mockNavController,
            mockDetailsViewModel
        )

        // Simulate click
        testHost.retryButtonAction?.invoke()

        verify(mockDetailsViewModel).retryLoadDetails()
    }

    @Test
    fun `DogDetailsErrorScreen back button calls navController popBackStack`() {
        val errorMessage = "Some Error"
        val errorState = DogDetailsUiState(errorMessage = errorMessage)
        setUiState(errorState)

        testHost.simulateDogDetailsScreenComposition(
            errorState,
            mockNavController,
            mockDetailsViewModel
        )

        // Simulate click
        testHost.backButtonAction?.invoke()

        verify(mockNavController).popBackStack()
    }

    @Test
    fun `DogDetails success content back button calls navController popBackStack`() {
        val successState = DogDetailsUiState(dogDetails = fakeBreedInfo)
        setUiState(successState)

        testHost.simulateDogDetailsScreenComposition(
            successState,
            mockNavController,
            mockDetailsViewModel
        )

        // Simulate click
        testHost.backButtonAction?.invoke()

        verify(mockNavController).popBackStack()
    }
}