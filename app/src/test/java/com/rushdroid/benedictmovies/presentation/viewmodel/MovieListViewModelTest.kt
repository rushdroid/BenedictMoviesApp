package com.rushdroid.benedictmovies.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.rushdroid.benedictmovies.core.constants.Constants
import com.rushdroid.benedictmovies.domain.model.Movie
import com.rushdroid.benedictmovies.domain.usecase.GetMoviesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for MovieListViewModel covering business logic scenarios.
 * Tests cover success cases, error handling, loading states, and data transformation.
 */
@ExperimentalCoroutinesApi
class MovieListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val mockGetMoviesUseCase: GetMoviesUseCase = mockk()

    private lateinit var viewModel: MovieListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load Benedict Cumberbatch movies successfully`() = testScope.runTest {
        // Given
        val expectedMovies = createSampleMovies()
        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.success(expectedMovies)

        // When
        viewModel = MovieListViewModel(mockGetMoviesUseCase)

        // Collect the flow to trigger onStart
        val job = backgroundScope.launch {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertThat(uiState.movies).isEqualTo(expectedMovies)
        assertThat(uiState.isLoading).isFalse()
        assertThat(uiState.error).isNull()

        coVerify { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) }

        job.cancel()
    }

    @Test
    fun `init should handle movies loading failure`() = testScope.runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.failure(RuntimeException(errorMessage))

        // When
        viewModel = MovieListViewModel(mockGetMoviesUseCase)

        // Collect the flow to trigger onStart
        val job = backgroundScope.launch {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertThat(uiState.movies).isEmpty()
        assertThat(uiState.isLoading).isFalse()
        assertThat(uiState.error).isEqualTo(errorMessage)

        job.cancel()
    }

    @Test
    fun `loadBenedictCumberbatchMovies should reload movies`() = testScope.runTest {
        // Given
        val initialMovies = listOf(createSampleMovie(1, "Initial Movie"))
        val reloadedMovies = listOf(
            createSampleMovie(1, "Movie 1"),
            createSampleMovie(2, "Movie 2")
        )

        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returnsMany listOf(
            Result.success(initialMovies),
            Result.success(reloadedMovies)
        )

        viewModel = MovieListViewModel(mockGetMoviesUseCase)
        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // Verify initial state
        val initialState = viewModel.uiState.value
        assertThat(initialState.movies).hasSize(1)

        // When
        viewModel.loadBenedictCumberbatchMovies()
        advanceUntilIdle()

        // Then
        val reloadedState = viewModel.uiState.value
        assertThat(reloadedState.movies).hasSize(2)
        assertThat(reloadedState.movies).isEqualTo(reloadedMovies)
        assertThat(reloadedState.isLoading).isFalse()

        coVerify(exactly = 2) { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) }
        job.cancel()
    }

    @Test
    fun `clearError should clear error state`() = testScope.runTest {
        // Given
        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.failure(RuntimeException("General error"))

        viewModel = MovieListViewModel(mockGetMoviesUseCase)
        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // Verify error is set
        val stateWithError = viewModel.uiState.value
        assertThat(stateWithError.error).isNotNull()

        // When
        viewModel.clearError()
        advanceUntilIdle()

        // Then
        val clearedState = viewModel.uiState.value
        assertThat(clearedState.error).isNull()
        job.cancel()
    }

    @Test
    fun `retry loading movies after initial failure should succeed`() = testScope.runTest {
        // Given
        val expectedMovies = createSampleMovies()
        val errorMessage = "Network error"

        // First call fails, second call succeeds
        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returnsMany listOf(
            Result.failure(RuntimeException(errorMessage)),
            Result.success(expectedMovies)
        )

        viewModel = MovieListViewModel(mockGetMoviesUseCase)
        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // Verify initial error state
        val errorState = viewModel.uiState.value
        assertThat(errorState.error).isEqualTo(errorMessage)
        assertThat(errorState.movies).isEmpty()

        // When - Clear error and retry
        viewModel.clearError()
        viewModel.loadBenedictCumberbatchMovies()
        advanceUntilIdle()

        // Then - Should succeed on retry
        val successState = viewModel.uiState.value
        assertThat(successState.error).isNull()
        assertThat(successState.movies).isEqualTo(expectedMovies)
        assertThat(successState.movies).hasSize(3)
        assertThat(successState.isLoading).isFalse()

        coVerify(exactly = 2) { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) }
        job.cancel()
    }

    @Test
    fun `multiple retry attempts should work correctly`() = testScope.runTest {
        // Given
        val expectedMovies = createSampleMovies()

        // First two calls fail, third call succeeds
        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returnsMany listOf(
            Result.failure(RuntimeException("Network error 1")),
            Result.failure(RuntimeException("Network error 2")),
            Result.success(expectedMovies)
        )

        viewModel = MovieListViewModel(mockGetMoviesUseCase)
        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // First failure
        assertThat(viewModel.uiState.value.error).isNotNull()

        // When - First retry (still fails)
        viewModel.clearError()
        viewModel.loadBenedictCumberbatchMovies()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.error).isEqualTo("Network error 2")

        // When - Second retry (succeeds)
        viewModel.clearError()
        viewModel.loadBenedictCumberbatchMovies()
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertThat(finalState.error).isNull()
        assertThat(finalState.movies).isEqualTo(expectedMovies)

        coVerify(exactly = 3) { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) }
        job.cancel()
    }

    @Test
    fun `clearError should not reload data automatically`() = testScope.runTest {
        // Given
        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.failure(RuntimeException("Error"))

        viewModel = MovieListViewModel(mockGetMoviesUseCase)
        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.error).isNotNull()

        // When - Only clear error without reloading
        viewModel.clearError()
        advanceUntilIdle()

        // Then - Error should be cleared but no additional API call
        val state = viewModel.uiState.value
        assertThat(state.error).isNull()

        // Verify only initial load was attempted
        coVerify(exactly = 1) { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) }
        job.cancel()
    }

    @Test
    fun `error state should persist until cleared or successful retry`() = testScope.runTest {
        // Given
        val errorMessage = "Persistent error"
        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.failure(RuntimeException(errorMessage))

        viewModel = MovieListViewModel(mockGetMoviesUseCase)
        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // When - Error occurs
        val errorState1 = viewModel.uiState.value
        assertThat(errorState1.error).isEqualTo(errorMessage)

        // Collect state multiple times - error should persist
        advanceUntilIdle()
        val errorState2 = viewModel.uiState.value
        assertThat(errorState2.error).isEqualTo(errorMessage)

        // When - Clear error
        viewModel.clearError()
        advanceUntilIdle()

        // Then - Error should be cleared
        val clearedState = viewModel.uiState.value
        assertThat(clearedState.error).isNull()
        job.cancel()
    }

    @Test
    fun `loading state should be set correctly during movie loading`() = testScope.runTest {
        // Given
        val expectedMovies = createSampleMovies()
        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.success(expectedMovies)

        viewModel = MovieListViewModel(mockGetMoviesUseCase)
        val job = backgroundScope.launch { viewModel.uiState.collect { } }

        // When - Loading happens automatically on init
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertThat(finalState.isLoading).isFalse()
        assertThat(finalState.movies).isEqualTo(expectedMovies)
        assertThat(finalState.error).isNull()

        job.cancel()
    }

    @Test
    fun `empty movie list should be handled correctly`() = testScope.runTest {
        // Given
        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.success(emptyList())

        // When
        viewModel = MovieListViewModel(mockGetMoviesUseCase)
        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertThat(uiState.movies).isEmpty()
        assertThat(uiState.isLoading).isFalse()
        assertThat(uiState.error).isNull()

        job.cancel()
    }

    // Helper methods for creating sample data
    private fun createSampleMovies(): List<Movie> {
        return listOf(
            createSampleMovie(1, "Doctor Strange"),
            createSampleMovie(2, "The Imitation Game"),
            createSampleMovie(3, "Sherlock Holmes")
        )
    }

    private fun createSampleMovie(id: Int, title: String): Movie {
        return Movie(
            id = id,
            title = title,
            overview = "Sample overview for $title",
            posterPath = "/sample_poster_$id.jpg",
            backdropPath = "/sample_backdrop_$id.jpg",
            releaseDate = "2023-01-01",
            voteAverage = 7.5,
            voteCount = 1000,
            popularity = 100.0
        )
    }
}

