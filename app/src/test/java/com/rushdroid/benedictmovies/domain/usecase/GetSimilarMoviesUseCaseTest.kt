package com.rushdroid.benedictmovies.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.rushdroid.benedictmovies.domain.model.Movie
import com.rushdroid.benedictmovies.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetSimilarMoviesUseCaseTest {

    private lateinit var movieRepository: MovieRepository
    private lateinit var getSimilarMoviesUseCase: GetSimilarMoviesUseCase

    private val testMovieId = 123
    private val testSimilarMovies = listOf(
        Movie(
            id = 456,
            title = "Doctor Strange in the Multiverse of Madness",
            overview = "A journey through the multiverse...",
            posterPath = "/poster1.jpg",
            backdropPath = "/backdrop1.jpg",
            releaseDate = "2022-05-06",
            voteAverage = 7.5,
            voteCount = 5000,
            popularity = 150.0
        ),
        Movie(
            id = 789,
            title = "Thor: Ragnarok",
            overview = "Thor must escape from Sakaar...",
            posterPath = "/poster2.jpg",
            backdropPath = "/backdrop2.jpg",
            releaseDate = "2017-10-25",
            voteAverage = 7.9,
            voteCount = 18000,
            popularity = 180.0
        ),
        Movie(
            id = 321,
            title = "The Avengers",
            overview = "Earth's mightiest heroes...",
            posterPath = "/poster3.jpg",
            backdropPath = "/backdrop3.jpg",
            releaseDate = "2012-04-25",
            voteAverage = 8.0,
            voteCount = 25000,
            popularity = 200.0
        )
    )

    @Before
    fun setup() {
        movieRepository = mockk()
        getSimilarMoviesUseCase = GetSimilarMoviesUseCase(movieRepository)
    }

    @Test
    fun `when repository returns success, use case should return success with similar movies`() = runTest {
        // Given
        coEvery { movieRepository.getSimilarMovies(testMovieId) } returns Result.success(testSimilarMovies)

        // When
        val result = getSimilarMoviesUseCase(testMovieId)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(testSimilarMovies)
        assertThat(result.getOrNull()).hasSize(3)
        coVerify(exactly = 1) { movieRepository.getSimilarMovies(testMovieId) }
    }

    @Test
    fun `when repository returns failure, use case should return failure`() = runTest {
        // Given
        val exception = Exception("Network error")
        coEvery { movieRepository.getSimilarMovies(testMovieId) } returns Result.failure(exception)

        // When
        val result = getSimilarMoviesUseCase(testMovieId)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Network error")
        coVerify(exactly = 1) { movieRepository.getSimilarMovies(testMovieId) }
    }

    @Test
    fun `when repository returns empty list, use case should return success with empty list`() = runTest {
        // Given
        coEvery { movieRepository.getSimilarMovies(testMovieId) } returns Result.success(emptyList())

        // When
        val result = getSimilarMoviesUseCase(testMovieId)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEmpty()
        coVerify(exactly = 1) { movieRepository.getSimilarMovies(testMovieId) }
    }

    @Test
    fun `when repository returns single similar movie, use case should return success with one movie`() = runTest {
        // Given
        val singleMovie = listOf(testSimilarMovies.first())
        coEvery { movieRepository.getSimilarMovies(testMovieId) } returns Result.success(singleMovie)

        // When
        val result = getSimilarMoviesUseCase(testMovieId)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).hasSize(1)
        assertThat(result.getOrNull()?.first()?.title).isEqualTo("Doctor Strange in the Multiverse of Madness")
        coVerify(exactly = 1) { movieRepository.getSimilarMovies(testMovieId) }
    }

    @Test
    fun `when repository throws exception, use case should return failure`() = runTest {
        // Given
        val exception = RuntimeException("Unexpected error")
        coEvery { movieRepository.getSimilarMovies(testMovieId) } returns Result.failure(exception)

        // When
        val result = getSimilarMoviesUseCase(testMovieId)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
        coVerify(exactly = 1) { movieRepository.getSimilarMovies(testMovieId) }
    }

    @Test
    fun `when multiple calls with different movie IDs, use case should return correct results`() = runTest {
        // Given
        val movieId1 = 123
        val movieId2 = 456
        val movies1 = listOf(testSimilarMovies[0])
        val movies2 = listOf(testSimilarMovies[1], testSimilarMovies[2])

        coEvery { movieRepository.getSimilarMovies(movieId1) } returns Result.success(movies1)
        coEvery { movieRepository.getSimilarMovies(movieId2) } returns Result.success(movies2)

        // When
        val result1 = getSimilarMoviesUseCase(movieId1)
        val result2 = getSimilarMoviesUseCase(movieId2)

        // Then
        assertThat(result1.getOrNull()).hasSize(1)
        assertThat(result2.getOrNull()).hasSize(2)
        coVerify(exactly = 1) { movieRepository.getSimilarMovies(movieId1) }
        coVerify(exactly = 1) { movieRepository.getSimilarMovies(movieId2) }
    }
}

