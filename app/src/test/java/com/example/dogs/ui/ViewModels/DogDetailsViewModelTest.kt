package com.example.dogs.ui.ViewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.dogs.api.DogApi
import com.example.dogs.api.DogBreedData
import com.example.dogs.api.DogBreedDetailResponse
import com.example.dogs.ui.NavDestinations
import com.example.dogs.ui.viewModels.DogDetailsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.IOException

@ExperimentalCoroutinesApi
class DogDetailsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: DogDetailsViewModel
    private lateinit var mockDogApi: DogApi
    private lateinit var savedStateHandle: SavedStateHandle


    private val testDogId = "1"

    private val testDogBreedData =
        DogBreedData(id = testDogId, type = "breed", attributes = fakeBreedInfo)
    private val successResponse = DogBreedDetailResponse(data = testDogBreedData)


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockDogApi = mock()
    }

    // Helper to initialize ViewModel with a specific dogId in SavedStateHandle
    private fun initializeViewModel(dogId: String?) {
        savedStateHandle = SavedStateHandle().apply {
            set(NavDestinations.DOG_ID_ARG, dogId)
        }
        viewModel = DogDetailsViewModel(savedStateHandle, mockDogApi)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }



    @Test
    fun `init with valid dogId loads details successfully`() = runTest(testDispatcher) {
        whenever(mockDogApi.getDogBreedDetailsById(testDogId)).thenReturn(successResponse)

        initializeViewModel(testDogId) // ViewModel's init block will trigger load

        viewModel.uiState.test {
            var emittedItem = awaitItem()
            if (!emittedItem.isLoading && emittedItem.dogDetails == null) {
                emittedItem = awaitItem()
            }
            assertTrue("Should be loading", emittedItem.isLoading)
            assertNull("Error message should be null during load", emittedItem.errorMessage)

            // Success state
            val successState = awaitItem()
            assertFalse("Should not be loading after success", successState.isLoading)
            assertEquals(fakeBreedInfo, successState.dogDetails)
            assertNull("Error message should be null on success", successState.errorMessage)

            cancelAndConsumeRemainingEvents()
        }
        verify(mockDogApi).getDogBreedDetailsById(testDogId)
    }



    @Test
    fun `init with blank dogId sets error state`() = runTest(testDispatcher) {
        initializeViewModel("") // Blank dogId

        val uiState = viewModel.uiState.value // Get current state after init
        assertFalse(uiState.isLoading)
        assertNull(uiState.dogDetails)
        assertEquals("Dog id is blank", uiState.errorMessage)
    }

    @Test
    fun `init with null dogId sets error state`() = runTest(testDispatcher) {
        initializeViewModel(null) // Null dogId

        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertNull(uiState.dogDetails)
        assertEquals("Dog id is blank", uiState.errorMessage)
    }

}