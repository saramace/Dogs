package com.example.dogs.ui.ViewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.dogs.api.DogRepository
import com.example.dogs.dogScreen.DogBreedsListUiState
import com.example.dogs.ui.viewModels.DogListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever


@ExperimentalCoroutinesApi
class DogListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: DogListViewModel
    private lateinit var mockRepository: DogRepository


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mock()
        whenever(mockRepository.getDogBreedsPager()).thenReturn(emptyFlow())
        viewModel = DogListViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `inital ui state initalizes with default values`()
    {
        val expectedInitialState = DogBreedsListUiState()
        assertEquals(expectedInitialState, viewModel.uiState.value)
    }


    @Test
    fun `setLoadingInital false`()= runTest(testDispatcher) {
        viewModel.setLoadingInitial(true)
        advanceUntilIdle() // isInitialLoading = true, isEmptyState = false

        val previousIsEmptyState = viewModel.uiState.value.isEmptyState // Should be false

        viewModel.setLoadingInitial(false)
        advanceUntilIdle()

        viewModel.uiState.test {
            val updatedState = awaitItem()
            Assert.assertFalse("isInitialLoading should be false", updatedState.isInitialLoading)
            assertEquals("isEmptyState should be preserved", previousIsEmptyState, updatedState.isEmptyState)
            Assert.assertFalse("isEmptyState should have been preserved as false", updatedState.isEmptyState) // More specific
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `setLoadingInital true`()= runTest(testDispatcher) {
        viewModel.setLoadingInitial(false)
        advanceUntilIdle() // isInitialLoading = true, isEmptyState = true

        val previousIsEmptyState = viewModel.uiState.value.isEmptyState // Should be false

        viewModel.setLoadingInitial(true)
        advanceUntilIdle()

        viewModel.uiState.test {
            val updatedState = awaitItem()
            Assert.assertTrue("isInitialLoading should be true", updatedState.isInitialLoading)
            assertEquals("isEmptyState should be preserved", previousIsEmptyState, updatedState.isEmptyState)
            Assert.assertFalse("isEmptyState should have been preserved as false", updatedState.isEmptyState) // More specific
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `updateEmptyState false - when isInitialLoading was true and screenError existed`() = runTest(testDispatcher) {
        viewModel.setLoadingInitial(true) // isInitialLoading = true, isEmptyState = false
        advanceUntilIdle()

        viewModel.updateEmptyState(false)
        advanceUntilIdle()

        viewModel.uiState.test {
            val updatedState = awaitItem()
            Assert.assertFalse(updatedState.isEmptyState)
            Assert.assertFalse("isInitialLoading should be explicitly set to false", updatedState.isInitialLoading)
            Assert.assertNull("screenError should be cleared to null", updatedState.screenError)
            cancelAndConsumeRemainingEvents()
        }
    }


    @Test
    fun `updateEmptyState true - when isInitialLoading was true and screenError existed`() = runTest(testDispatcher) {


 val initialErrorState = DogBreedsListUiState(
     isEmptyState = false, // Does not matter for this path
     screenError = "Previous Error"
    )

 viewModel.setLoadingInitial(true) // isInitialLoading = true, isEmptyState = false
    advanceUntilIdle()

    viewModel.updateEmptyState(true)
    advanceUntilIdle()

    viewModel.uiState.test {
        val updatedState = awaitItem()
        Assert.assertTrue(updatedState.isEmptyState)
        Assert.assertFalse("isInitialLoading should be explicitly set to false", updatedState.isInitialLoading)
        Assert.assertNull("screenError should be cleared to null", updatedState.screenError)
        cancelAndConsumeRemainingEvents()
    }
}
}