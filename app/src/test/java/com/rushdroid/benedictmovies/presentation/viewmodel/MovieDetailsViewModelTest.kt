package com.rushdroid.benedictmovies.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.rushdroid.benedictmovies.domain.model.Genre
import com.rushdroid.benedictmovies.domain.model.Movie
import com.rushdroid.benedictmovies.domain.model.MovieDetail
import com.rushdroid.benedictmovies.domain.usecase.GetMovieDetailUseCase
import com.rushdroid.benedictmovies.domain.usecase.GetSimilarMoviesUseCase
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
 * Unit tests for MovieDetailsViewModel covering business logic scenarios.
 * Tests cover success cases, error handling, loading states, and data transformation.
 */
@ExperimentalCoroutinesApi
class MovieDetailsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val mockGetMovieDetailUseCase: GetMovieDetailUseCase = mockk()
    private val mockGetSimilarMoviesUseCase: GetSimilarMoviesUseCase = mockk()

    private lateinit var viewModel: MovieDetailsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MovieDetailsViewModel(mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMovieDetail should successfully load movie details`() = testScope.runTest {
        // Given
        val movieId = 123
        val expectedMovieDetail = createSampleMovieDetail()
        coEvery { mockGetMovieDetailUseCase(movieId) } returns Result.success(expectedMovieDetail)
        coEvery { mockGetSimilarMoviesUseCase(movieId) } returns Result.success(emptyList())

        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // When
        viewModel.loadMovieDetail(movieId)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertThat(uiState.selectedMovieDetail).isEqualTo(expectedMovieDetail)
        assertThat(uiState.isLoadingDetail).isFalse()
        assertThat(uiState.detailError).isNull()

        coVerify { mockGetMovieDetailUseCase(movieId) }
        job.cancel()
    }

    @Test
    fun `loadMovieDetail should handle failure`() = testScope.runTest {
        // Given
        val movieId = 123
        val errorMessage = "Movie not found"
        coEvery { mockGetMovieDetailUseCase(movieId) } returns Result.failure(RuntimeException(errorMessage))

        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // When
        viewModel.loadMovieDetail(movieId)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertThat(uiState.selectedMovieDetail).isNull()
        assertThat(uiState.isLoadingDetail).isFalse()
        assertThat(uiState.detailError).isEqualTo(errorMessage)
        job.cancel()
    }

    @Test
    fun `loadMovieDetail should set loading state correctly`() = testScope.runTest {
        // Given
        val movieId = 123
        coEvery { mockGetMovieDetailUseCase(movieId) } returns Result.success(createSampleMovieDetail())
        coEvery { mockGetSimilarMoviesUseCase(movieId) } returns Result.success(emptyList())

        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // Verify initial state
        val initialState = viewModel.uiState.value
        assertThat(initialState.isLoadingDetail).isFalse()
        assertThat(initialState.selectedMovieDetail).isNull()

        // When - Call loadMovieDetail
        viewModel.loadMovieDetail(movieId)

        // Then - Verify final state after operation completes
        advanceUntilIdle()

        val finalState = viewModel.uiState.value
        assertThat(finalState.isLoadingDetail).isFalse()
        assertThat(finalState.selectedMovieDetail).isNotNull()
        assertThat(finalState.selectedMovieDetail?.id).isEqualTo(movieId)
        assertThat(finalState.detailError).isNull()

        // Verify the use case was called
        coVerify { mockGetMovieDetailUseCase(movieId) }
        job.cancel()
    }

    @Test
    fun `loadMovieDetail should automatically load similar movies on success`() = testScope.runTest {
        // Given
        val movieId = 123
        val movieDetail = createSampleMovieDetail()
        val similarMovies = createSampleMovies()

        coEvery { mockGetMovieDetailUseCase(movieId) } returns Result.success(movieDetail)
        coEvery { mockGetSimilarMoviesUseCase(movieId) } returns Result.success(similarMovies)

        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // When
        viewModel.loadMovieDetail(movieId)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertThat(uiState.selectedMovieDetail).isEqualTo(movieDetail)
        assertThat(uiState.similarMovies).isEqualTo(similarMovies)
        assertThat(uiState.isLoadingSimilar).isFalse()
        assertThat(uiState.similarError).isNull()

        coVerify { mockGetMovieDetailUseCase(movieId) }
        coVerify { mockGetSimilarMoviesUseCase(movieId) }
        job.cancel()
    }

    @Test
    fun `loadMovieDetail should not load similar movies when detail loading fails`() = testScope.runTest {
        // Given
        val movieId = 555
        val errorMessage = "Movie detail not found"
        coEvery { mockGetMovieDetailUseCase(movieId) } returns Result.failure(RuntimeException(errorMessage))

        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // When
        viewModel.loadMovieDetail(movieId)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertThat(uiState.selectedMovieDetail).isNull()
        assertThat(uiState.detailError).isEqualTo(errorMessage)
        assertThat(uiState.similarMovies).isEmpty()

        coVerify { mockGetMovieDetailUseCase(movieId) }
        coVerify(exactly = 0) { mockGetSimilarMoviesUseCase(any()) }
        job.cancel()
    }

    @Test
    fun `multiple consecutive loadMovieDetail calls should handle correctly`() = testScope.runTest {
        // Given
        val movieId1 = 123
        val movieId2 = 456
        val movieDetail1 = createSampleMovieDetail(movieId1, "Movie 1")
        val movieDetail2 = createSampleMovieDetail(movieId2, "Movie 2")

        coEvery { mockGetMovieDetailUseCase(movieId1) } returns Result.success(movieDetail1)
        coEvery { mockGetMovieDetailUseCase(movieId2) } returns Result.success(movieDetail2)
        coEvery { mockGetSimilarMoviesUseCase(any()) } returns Result.success(emptyList())

        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // When
        viewModel.loadMovieDetail(movieId1)
        advanceUntilIdle()

        val state1 = viewModel.uiState.value
        assertThat(state1.selectedMovieDetail?.id).isEqualTo(movieId1)

        viewModel.loadMovieDetail(movieId2)
        advanceUntilIdle()

        // Then
        val state2 = viewModel.uiState.value
        assertThat(state2.selectedMovieDetail?.id).isEqualTo(movieId2)
        assertThat(state2.selectedMovieDetail?.title).isEqualTo("Movie 2")

        coVerify { mockGetMovieDetailUseCase(movieId1) }
        coVerify { mockGetMovieDetailUseCase(movieId2) }
        job.cancel()
    }

    // Similar Movies Tests
    @Test
    fun `loadSimilarMovies should load similar movies successfully`() = testScope.runTest {
        // Given
        val movieId = 456
        val similarMovies = listOf(
            createSampleMovie(101, "Similar Movie 1"),
            createSampleMovie(102, "Similar Movie 2"),
            createSampleMovie(103, "Similar Movie 3")
        )

        coEvery { mockGetSimilarMoviesUseCase(movieId) } returns Result.success(similarMovies)

        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // When
        viewModel.loadSimilarMovies(movieId)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertThat(uiState.similarMovies).hasSize(3)
        assertThat(uiState.similarMovies).isEqualTo(similarMovies)
        assertThat(uiState.isLoadingSimilar).isFalse()
        assertThat(uiState.similarError).isNull()

        coVerify { mockGetSimilarMoviesUseCase(movieId) }
        job.cancel()
    }

    @Test
    fun `loadSimilarMovies should handle empty similar movies list`() = testScope.runTest {
        // Given
        val movieId = 789
        coEvery { mockGetSimilarMoviesUseCase(movieId) } returns Result.success(emptyList())

        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // When
        viewModel.loadSimilarMovies(movieId)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertThat(uiState.similarMovies).isEmpty()
        assertThat(uiState.isLoadingSimilar).isFalse()
        assertThat(uiState.similarError).isNull()

        coVerify { mockGetSimilarMoviesUseCase(movieId) }
        job.cancel()
    }

    @Test
    fun `loadSimilarMovies should handle failure`() = testScope.runTest {
        // Given
        val movieId = 321
        val errorMessage = "Failed to load similar movies"
        coEvery { mockGetSimilarMoviesUseCase(movieId) } returns Result.failure(RuntimeException(errorMessage))

        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // When
        viewModel.loadSimilarMovies(movieId)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertThat(uiState.similarMovies).isEmpty()
        assertThat(uiState.isLoadingSimilar).isFalse()
        assertThat(uiState.similarError).isEqualTo(errorMessage)

        coVerify { mockGetSimilarMoviesUseCase(movieId) }
        job.cancel()
    }

    @Test
    fun `loading similar movies for different movie IDs should update state correctly`() = testScope.runTest {
        // Given
        val movieId1 = 111
        val movieId2 = 222
        val similarMovies1 = listOf(createSampleMovie(1, "Similar to Movie 1"))
        val similarMovies2 = listOf(createSampleMovie(2, "Similar to Movie 2"))

        coEvery { mockGetSimilarMoviesUseCase(movieId1) } returns Result.success(similarMovies1)
        coEvery { mockGetSimilarMoviesUseCase(movieId2) } returns Result.success(similarMovies2)

        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // When - Load similar movies for first movie
        viewModel.loadSimilarMovies(movieId1)
        advanceUntilIdle()

        val state1 = viewModel.uiState.value
        assertThat(state1.similarMovies).isEqualTo(similarMovies1)

        // When - Load similar movies for second movie
        viewModel.loadSimilarMovies(movieId2)
        advanceUntilIdle()

        // Then
        val state2 = viewModel.uiState.value
        assertThat(state2.similarMovies).isEqualTo(similarMovies2)
        assertThat(state2.similarMovies.first().title).isEqualTo("Similar to Movie 2")

        coVerify { mockGetSimilarMoviesUseCase(movieId1) }
        coVerify { mockGetSimilarMoviesUseCase(movieId2) }
        job.cancel()
    }

    // Error Handling Tests
    @Test
    fun `clearError should clear both detailError and similarError`() = testScope.runTest {
        // Given
        val movieId = 999
        coEvery { mockGetMovieDetailUseCase(movieId) } returns Result.failure(RuntimeException("Detail error"))
        coEvery { mockGetSimilarMoviesUseCase(movieId) } returns Result.failure(RuntimeException("Similar movies error"))

        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        viewModel.loadMovieDetail(movieId)
        advanceUntilIdle()

        viewModel.loadSimilarMovies(movieId)
        advanceUntilIdle()

        // Verify errors are set
        val stateWithErrors = viewModel.uiState.value
        assertThat(stateWithErrors.detailError).isNotNull()
        assertThat(stateWithErrors.similarError).isNotNull()

        // When
        viewModel.clearError()
        advanceUntilIdle()

        // Then
        val clearedState = viewModel.uiState.value
        assertThat(clearedState.detailError).isNull()
        assertThat(clearedState.similarError).isNull()
        job.cancel()
    }

    @Test
    fun `clearError should not reload data automatically`() = testScope.runTest {
        // Given
        val movieId = 123
        coEvery { mockGetMovieDetailUseCase(movieId) } returns Result.failure(RuntimeException("Error"))

        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        viewModel.loadMovieDetail(movieId)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.detailError).isNotNull()

        // When - Only clear error without reloading
        viewModel.clearError()
        advanceUntilIdle()

        // Then - Error should be cleared but no additional API call
        val state = viewModel.uiState.value
        assertThat(state.detailError).isNull()
        assertThat(state.similarError).isNull()

        // Verify only one load was attempted
        coVerify(exactly = 1) { mockGetMovieDetailUseCase(movieId) }
        job.cancel()
    }

    // Retry Functionality Tests
    @Test
    fun `retry loading movie detail after initial failure should succeed`() = testScope.runTest {
        // Given
        val movieId = 123
        val expectedMovieDetail = createSampleMovieDetail()
        val errorMessage = "Movie not found"

        // First call fails, second call succeeds
        coEvery { mockGetMovieDetailUseCase(movieId) } returnsMany listOf(
            Result.failure(RuntimeException(errorMessage)),
            Result.success(expectedMovieDetail)
        )
        coEvery { mockGetSimilarMoviesUseCase(movieId) } returns Result.success(emptyList())

        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // First attempt - should fail
        viewModel.loadMovieDetail(movieId)
        advanceUntilIdle()

        val errorState = viewModel.uiState.value
        assertThat(errorState.detailError).isEqualTo(errorMessage)
        assertThat(errorState.selectedMovieDetail).isNull()

        // When - Clear error and retry
        viewModel.clearError()
        viewModel.loadMovieDetail(movieId)
        advanceUntilIdle()

        // Then - Should succeed on retry
        val successState = viewModel.uiState.value
        assertThat(successState.detailError).isNull()
        assertThat(successState.selectedMovieDetail).isEqualTo(expectedMovieDetail)
        assertThat(successState.isLoadingDetail).isFalse()

        coVerify(exactly = 2) { mockGetMovieDetailUseCase(movieId) }
        job.cancel()
    }

    @Test
    fun `retry loading similar movies after initial failure should succeed`() = testScope.runTest {
        // Given
        val movieId = 456
        val expectedSimilarMovies = createSampleMovies()
        val errorMessage = "Failed to load similar movies"

        // First call fails, second call succeeds
        coEvery { mockGetSimilarMoviesUseCase(movieId) } returnsMany listOf(
            Result.failure(RuntimeException(errorMessage)),
            Result.success(expectedSimilarMovies)
        )

        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // First attempt - should fail
        viewModel.loadSimilarMovies(movieId)
        advanceUntilIdle()

        val errorState = viewModel.uiState.value
        assertThat(errorState.similarError).isEqualTo(errorMessage)
        assertThat(errorState.similarMovies).isEmpty()

        // When - Clear error and retry
        viewModel.clearError()
        viewModel.loadSimilarMovies(movieId)
        advanceUntilIdle()

        // Then - Should succeed on retry
        val successState = viewModel.uiState.value
        assertThat(successState.similarError).isNull()
        assertThat(successState.similarMovies).isEqualTo(expectedSimilarMovies)
        assertThat(successState.isLoadingSimilar).isFalse()

        coVerify(exactly = 2) { mockGetSimilarMoviesUseCase(movieId) }
        job.cancel()
    }

    @Test
    fun `retry with different movie ID should work correctly`() = testScope.runTest {
        // Given
        val movieId1 = 123
        val movieId2 = 456
        val movieDetail2 = createSampleMovieDetail(movieId2, "Movie 2")

        coEvery { mockGetMovieDetailUseCase(movieId1) } returns Result.failure(RuntimeException("Error for movie 1"))
        coEvery { mockGetMovieDetailUseCase(movieId2) } returns Result.success(movieDetail2)
        coEvery { mockGetSimilarMoviesUseCase(movieId2) } returns Result.success(emptyList())

        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // First attempt with movieId1 - fails
        viewModel.loadMovieDetail(movieId1)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.detailError).isEqualTo("Error for movie 1")

        // When - Retry with different movie ID
        viewModel.clearError()
        viewModel.loadMovieDetail(movieId2)
        advanceUntilIdle()

        // Then - Should load different movie successfully
        val state = viewModel.uiState.value
        assertThat(state.detailError).isNull()
        assertThat(state.selectedMovieDetail?.id).isEqualTo(movieId2)
        assertThat(state.selectedMovieDetail?.title).isEqualTo("Movie 2")

        coVerify { mockGetMovieDetailUseCase(movieId1) }
        coVerify { mockGetMovieDetailUseCase(movieId2) }
        job.cancel()
    }

    @Test
    fun `error state should persist until cleared or successful retry`() = testScope.runTest {
        // Given
        val movieId = 123
        val errorMessage = "Persistent error"
        coEvery { mockGetMovieDetailUseCase(movieId) } returns Result.failure(RuntimeException(errorMessage))

        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // When - Error occurs
        viewModel.loadMovieDetail(movieId)
        advanceUntilIdle()

        val errorState1 = viewModel.uiState.value
        assertThat(errorState1.detailError).isEqualTo(errorMessage)

        // Collect state multiple times - error should persist
        advanceUntilIdle()
        val errorState2 = viewModel.uiState.value
        assertThat(errorState2.detailError).isEqualTo(errorMessage)

        // When - Clear error
        viewModel.clearError()
        advanceUntilIdle()

        // Then - Error should be cleared
        val clearedState = viewModel.uiState.value
        assertThat(clearedState.detailError).isNull()
        job.cancel()
    }

    @Test
    fun `multiple retry attempts should work correctly`() = testScope.runTest {
        // Given
        val movieId = 123
        val expectedMovieDetail = createSampleMovieDetail()

        // First two calls fail, third call succeeds
        coEvery { mockGetMovieDetailUseCase(movieId) } returnsMany listOf(
            Result.failure(RuntimeException("Network error 1")),
            Result.failure(RuntimeException("Network error 2")),
            Result.success(expectedMovieDetail)
        )
        coEvery { mockGetSimilarMoviesUseCase(movieId) } returns Result.success(emptyList())

        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // First failure
        viewModel.loadMovieDetail(movieId)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.detailError).isNotNull()

        // When - First retry (still fails)
        viewModel.clearError()
        viewModel.loadMovieDetail(movieId)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.detailError).isEqualTo("Network error 2")

        // When - Second retry (succeeds)
        viewModel.clearError()
        viewModel.loadMovieDetail(movieId)
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertThat(finalState.detailError).isNull()
        assertThat(finalState.selectedMovieDetail).isEqualTo(expectedMovieDetail)

        coVerify(exactly = 3) { mockGetMovieDetailUseCase(movieId) }
        job.cancel()
    }

    // State Validation Tests
    @Test
    fun `initial state should have correct default values`() = testScope.runTest {
        // Given & When
        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        val initialState = viewModel.uiState.value

        // Then
        assertThat(initialState.selectedMovieDetail).isNull()
        assertThat(initialState.similarMovies).isEmpty()
        assertThat(initialState.isLoadingDetail).isFalse()
        assertThat(initialState.isLoadingSimilar).isFalse()
        assertThat(initialState.detailError).isNull()
        assertThat(initialState.similarError).isNull()

        job.cancel()
    }

    @Test
    fun `similar movies should be loaded after movie detail success`() = testScope.runTest {
        // Given
        val movieId = 123
        val movieDetail = createSampleMovieDetail()
        val similarMovies = createSampleMovies()

        coEvery { mockGetMovieDetailUseCase(movieId) } returns Result.success(movieDetail)
        coEvery { mockGetSimilarMoviesUseCase(movieId) } returns Result.success(similarMovies)

        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // When
        viewModel.loadMovieDetail(movieId)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertThat(uiState.selectedMovieDetail).isNotNull()
        assertThat(uiState.similarMovies).isNotEmpty()
        assertThat(uiState.similarMovies).hasSize(3)

        // Verify both use cases were called in sequence
        coVerify(ordering = io.mockk.Ordering.ORDERED) {
            mockGetMovieDetailUseCase(movieId)
            mockGetSimilarMoviesUseCase(movieId)
        }
        job.cancel()
    }

    @Test
    fun `similar movies error should not affect movie detail state`() = testScope.runTest {
        // Given
        val movieId = 123
        val movieDetail = createSampleMovieDetail()
        val errorMessage = "Failed to load similar movies"

        coEvery { mockGetMovieDetailUseCase(movieId) } returns Result.success(movieDetail)
        coEvery { mockGetSimilarMoviesUseCase(movieId) } returns Result.failure(RuntimeException(errorMessage))

        val job = backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()

        // When
        viewModel.loadMovieDetail(movieId)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertThat(uiState.selectedMovieDetail).isEqualTo(movieDetail)
        assertThat(uiState.detailError).isNull()
        assertThat(uiState.similarError).isEqualTo(errorMessage)
        assertThat(uiState.similarMovies).isEmpty()

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

    private fun createSampleMovieDetail(id: Int = 123, title: String = "Sample Movie"): MovieDetail {
        return MovieDetail(
            id = id,
            title = title,
            overview = "A detailed overview of $title",
            posterPath = "/sample_poster.jpg",
            backdropPath = "/sample_backdrop.jpg",
            releaseDate = "2023-01-01",
            voteAverage = 8.0,
            voteCount = 2000,
            popularity = 150.0,
            runtime = 120,
            genres = listOf(
                Genre(id = 14, name = "Fantasy"),
                Genre(id = 28, name = "Action")
            ),
            tagline = "The sample tagline",
            budget = 165000000,
            revenue = 677718395,
            status = "Released",
            homepage = "https://example.com"
        )
    }
}

