package com.rushdroid.benedictmovies.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.rushdroid.benedictmovies.core.constants.Constants
import com.rushdroid.benedictmovies.domain.model.Genre
import com.rushdroid.benedictmovies.domain.model.Movie
import com.rushdroid.benedictmovies.domain.model.MovieDetail
import com.rushdroid.benedictmovies.domain.usecase.GetMovieDetailUseCase
import com.rushdroid.benedictmovies.domain.usecase.GetMoviesUseCase
import com.rushdroid.benedictmovies.domain.usecase.GetSimilarMoviesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
 * Unit tests for MovieViewModel covering business logic scenarios.
 * Tests cover success cases, error handling, loading states, and data transformation.
 */
@ExperimentalCoroutinesApi
class MovieViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val mockGetMoviesUseCase: GetMoviesUseCase = mockk()
    private val mockGetMovieDetailUseCase: GetMovieDetailUseCase = mockk()
    private val mockGetSimilarMoviesUseCase: GetSimilarMoviesUseCase = mockk()

    private lateinit var viewModel: MovieViewModel

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
        viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase,mockGetSimilarMoviesUseCase)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertThat(uiState.movies).isEqualTo(expectedMovies)
        assertThat(uiState.isLoading).isFalse()
        assertThat(uiState.error).isNull()

        coVerify { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) }
    }

    @Test
    fun `init should handle movies loading failure`() = testScope.runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.failure(RuntimeException(errorMessage))

        // When
        viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase,mockGetSimilarMoviesUseCase)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertThat(uiState.movies).isEmpty()
        assertThat(uiState.isLoading).isFalse()
        assertThat(uiState.error).isEqualTo(errorMessage)
    }

    @Test
    fun `loadMovieDetail should successfully load movie details`() = testScope.runTest {
        // Given
        val movieId = 123
        val expectedMovieDetail = createSampleMovieDetail()
        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.success(emptyList())
        coEvery { mockGetMovieDetailUseCase(movieId) } returns Result.success(expectedMovieDetail)
        coEvery { mockGetSimilarMoviesUseCase(movieId) } returns Result.success(emptyList())

        viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
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
    }

    @Test
    fun `loadMovieDetail should handle failure`() = testScope.runTest {
        // Given
        val movieId = 123
        val errorMessage = "Movie not found"
        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.success(emptyList())
        coEvery { mockGetMovieDetailUseCase(movieId) } returns Result.failure(RuntimeException(errorMessage))

         viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
        advanceUntilIdle()

        // When
        viewModel.loadMovieDetail(movieId)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertThat(uiState.selectedMovieDetail).isNull()
        assertThat(uiState.isLoadingDetail).isFalse()
        assertThat(uiState.detailError).isEqualTo(errorMessage)
    }

    @Test
    fun `loadMovieDetail should set loading state correctly`() = testScope.runTest {
        // Given
        val movieId = 123

        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.success(emptyList())
        coEvery { mockGetMovieDetailUseCase(movieId) } returns Result.success(createSampleMovieDetail())
        coEvery { mockGetSimilarMoviesUseCase(movieId) } returns Result.success(emptyList())

         viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
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
    }

    @Test
    fun `clearError should clear both error and detailError`() = testScope.runTest {
        // Given
        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.failure(RuntimeException("General error"))
        coEvery { mockGetMovieDetailUseCase(any()) } returns Result.failure(RuntimeException("Detail error"))

         viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
        advanceUntilIdle()

        viewModel.loadMovieDetail(123)
        advanceUntilIdle()

        // Verify errors are set
        val stateWithErrors = viewModel.uiState.value
        assertThat(stateWithErrors.error).isNotNull()
        assertThat(stateWithErrors.detailError).isNotNull()

        // When
        viewModel.clearError()

        // Then
        val clearedState = viewModel.uiState.value
        assertThat(clearedState.error).isNull()
        assertThat(clearedState.detailError).isNull()
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

         viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
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
    }

    @Test
    fun `multiple consecutive loadMovieDetail calls should handle correctly`() = testScope.runTest {
        // Given
        val movieId1 = 123
        val movieId2 = 456
        val movieDetail1 = createSampleMovieDetail(movieId1, "Movie 1")
        val movieDetail2 = createSampleMovieDetail(movieId2, "Movie 2")

        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.success(emptyList())
        coEvery { mockGetMovieDetailUseCase(movieId1) } returns Result.success(movieDetail1)
        coEvery { mockGetMovieDetailUseCase(movieId2) } returns Result.success(movieDetail2)
        coEvery { mockGetSimilarMoviesUseCase(any()) } returns Result.success(emptyList())

        viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
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
    }

    // Similar Movies Tests
    @Test
    fun `loadMovieDetail should automatically load similar movies on success`() = testScope.runTest {
        // Given
        val movieId = 123
        val movieDetail = createSampleMovieDetail()
        val similarMovies = createSampleMovies()

        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.success(emptyList())
        coEvery { mockGetMovieDetailUseCase(movieId) } returns Result.success(movieDetail)
        coEvery { mockGetSimilarMoviesUseCase(movieId) } returns Result.success(similarMovies)

        viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
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
    }

    @Test
    fun `loadSimilarMovies should load similar movies successfully`() = testScope.runTest {
        // Given
        val movieId = 456
        val similarMovies = listOf(
            createSampleMovie(101, "Similar Movie 1"),
            createSampleMovie(102, "Similar Movie 2"),
            createSampleMovie(103, "Similar Movie 3")
        )

        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.success(emptyList())
        coEvery { mockGetSimilarMoviesUseCase(movieId) } returns Result.success(similarMovies)

        viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
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
    }

    @Test
    fun `loadSimilarMovies should handle empty similar movies list`() = testScope.runTest {
        // Given
        val movieId = 789
        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.success(emptyList())
        coEvery { mockGetSimilarMoviesUseCase(movieId) } returns Result.success(emptyList())

        viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
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
    }

    @Test
    fun `loadSimilarMovies should handle failure`() = testScope.runTest {
        // Given
        val movieId = 321
        val errorMessage = "Failed to load similar movies"
        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.success(emptyList())
        coEvery { mockGetSimilarMoviesUseCase(movieId) } returns Result.failure(RuntimeException(errorMessage))

        viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
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
    }

    @Test
    fun `loadMovieDetail should not load similar movies when detail loading fails`() = testScope.runTest {
        // Given
        val movieId = 555
        val errorMessage = "Movie detail not found"
        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.success(emptyList())
        coEvery { mockGetMovieDetailUseCase(movieId) } returns Result.failure(RuntimeException(errorMessage))

        viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
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
    }

    @Test
    fun `clearError should clear similar movies error as well`() = testScope.runTest {
        // Given
        val movieId = 999
        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.failure(RuntimeException("General error"))
        coEvery { mockGetMovieDetailUseCase(movieId) } returns Result.failure(RuntimeException("Detail error"))
        coEvery { mockGetSimilarMoviesUseCase(movieId) } returns Result.failure(RuntimeException("Similar movies error"))

        viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
        advanceUntilIdle()

        viewModel.loadSimilarMovies(movieId)
        advanceUntilIdle()

        // Verify errors are set
        val stateWithErrors = viewModel.uiState.value
        assertThat(stateWithErrors.error).isNotNull()
        assertThat(stateWithErrors.similarError).isNotNull()

        // When
        viewModel.clearError()

        // Then
        val clearedState = viewModel.uiState.value
        assertThat(clearedState.error).isNull()
        assertThat(clearedState.detailError).isNull()
        assertThat(clearedState.similarError).isNull()
    }

    @Test
    fun `loading similar movies for different movie IDs should update state correctly`() = testScope.runTest {
        // Given
        val movieId1 = 111
        val movieId2 = 222
        val similarMovies1 = listOf(createSampleMovie(1, "Similar to Movie 1"))
        val similarMovies2 = listOf(createSampleMovie(2, "Similar to Movie 2"))

        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.success(emptyList())
        coEvery { mockGetSimilarMoviesUseCase(movieId1) } returns Result.success(similarMovies1)
        coEvery { mockGetSimilarMoviesUseCase(movieId2) } returns Result.success(similarMovies2)

        viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
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
    }

    // Retry Functionality Tests
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

        viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
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
    }

    @Test
    fun `retry loading movie detail after initial failure should succeed`() = testScope.runTest {
        // Given
        val movieId = 123
        val expectedMovieDetail = createSampleMovieDetail()
        val errorMessage = "Movie not found"

        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.success(emptyList())

        // First call fails, second call succeeds
        coEvery { mockGetMovieDetailUseCase(movieId) } returnsMany listOf(
            Result.failure(RuntimeException(errorMessage)),
            Result.success(expectedMovieDetail)
        )
        coEvery { mockGetSimilarMoviesUseCase(movieId) } returns Result.success(emptyList())

        viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
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

        viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
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
    }

    @Test
    fun `clearError should not reload data automatically`() = testScope.runTest {
        // Given
        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.failure(RuntimeException("Error"))

        viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.error).isNotNull()

        // When - Only clear error without reloading
        viewModel.clearError()

        // Then - Error should be cleared but no additional API call
        val state = viewModel.uiState.value
        assertThat(state.error).isNull()
        assertThat(state.detailError).isNull()
        assertThat(state.similarError).isNull()

        // Verify only initial load was attempted
        coVerify(exactly = 1) { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) }
    }

    @Test
    fun `retry with different movie ID should work correctly`() = testScope.runTest {
        // Given
        val movieId1 = 123
        val movieId2 = 456
        val movieDetail1 = createSampleMovieDetail(movieId1, "Movie 1")
        val movieDetail2 = createSampleMovieDetail(movieId2, "Movie 2")

        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.success(emptyList())
        coEvery { mockGetMovieDetailUseCase(movieId1) } returns Result.failure(RuntimeException("Error for movie 1"))
        coEvery { mockGetMovieDetailUseCase(movieId2) } returns Result.success(movieDetail2)
        coEvery { mockGetSimilarMoviesUseCase(movieId2) } returns Result.success(emptyList())

        viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
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
    }

    @Test
    fun `error state should persist until cleared or successful retry`() = testScope.runTest {
        // Given
        val errorMessage = "Persistent error"
        coEvery { mockGetMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID) } returns Result.failure(RuntimeException(errorMessage))

        viewModel = MovieViewModel(mockGetMoviesUseCase, mockGetMovieDetailUseCase, mockGetSimilarMoviesUseCase)
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

        // Then - Error should be cleared
        val clearedState = viewModel.uiState.value
        assertThat(clearedState.error).isNull()
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
