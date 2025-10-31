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

class GetMoviesUseCaseTest {

    private lateinit var movieRepository: MovieRepository
    private lateinit var getMoviesUseCase: GetMoviesUseCase

    private val testPersonId = 71580
    private val testMovies = listOf(
        Movie(
            id = 1,
            title = "Doctor Strange",
            overview = "A former neurosurgeon embarks on a journey of healing...",
            posterPath = "/poster1.jpg",
            backdropPath = "/backdrop1.jpg",
            releaseDate = "2016-10-25",
            voteAverage = 7.5,
            voteCount = 15000,
            popularity = 85.0
        ),
        Movie(
            id = 2,
            title = "The Imitation Game",
            overview = "During World War II, the English mathematical genius...",
            posterPath = "/poster2.jpg",
            backdropPath = "/backdrop2.jpg",
            releaseDate = "2014-11-14",
            voteAverage = 8.0,
            voteCount = 20000,
            popularity = 92.0
        )
    )

    @Before
    fun setup() {
        movieRepository = mockk()
        getMoviesUseCase = GetMoviesUseCase(movieRepository)
    }

    @Test
    fun `when repository returns success, use case should return success with movies`() = runTest {
        // Given
        coEvery { movieRepository.getMoviesByPerson(testPersonId) } returns Result.success(testMovies)

        // When
        val result = getMoviesUseCase(testPersonId)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(testMovies)
        coVerify(exactly = 1) { movieRepository.getMoviesByPerson(testPersonId) }
    }

    @Test
    fun `when repository returns failure, use case should return failure`() = runTest {
        // Given
        val exception = Exception("Network error")
        coEvery { movieRepository.getMoviesByPerson(testPersonId) } returns Result.failure(exception)

        // When
        val result = getMoviesUseCase(testPersonId)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
        coVerify(exactly = 1) { movieRepository.getMoviesByPerson(testPersonId) }
    }

    @Test
    fun `when repository returns empty list, use case should return success with empty list`() = runTest {
        // Given
        coEvery { movieRepository.getMoviesByPerson(testPersonId) } returns Result.success(emptyList())

        // When
        val result = getMoviesUseCase(testPersonId)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEmpty()
        coVerify(exactly = 1) { movieRepository.getMoviesByPerson(testPersonId) }
    }
}
