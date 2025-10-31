package com.rushdroid.benedictmovies.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.rushdroid.benedictmovies.domain.model.Genre
import com.rushdroid.benedictmovies.domain.model.MovieDetail
import com.rushdroid.benedictmovies.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetMovieDetailUseCaseTest {

    private lateinit var movieRepository: MovieRepository
    private lateinit var getMovieDetailUseCase: GetMovieDetailUseCase

    private val testMovieId = 123
    private val testMovieDetail = MovieDetail(
        id = 123,
        title = "Doctor Strange",
        overview = "A former neurosurgeon embarks on a journey of healing...",
        posterPath = "/poster.jpg",
        backdropPath = "/backdrop.jpg",
        releaseDate = "2016-10-25",
        voteAverage = 7.5,
        voteCount = 15000,
        popularity = 85.0,
        runtime = 115,
        budget = 165000000,
        revenue = 677718395,
        tagline = "The impossibilities are endless",
        status = "Released",
        homepage = "https://www.marvel.com/movies/doctor-strange",
        genres = listOf(
            Genre(id = 14, name = "Fantasy"),
            Genre(id = 28, name = "Action")
        )
    )

    @Before
    fun setup() {
        movieRepository = mockk()
        getMovieDetailUseCase = GetMovieDetailUseCase(movieRepository)
    }

    @Test
    fun `when repository returns success, use case should return movie detail`() = runTest {
        // Given
        coEvery { movieRepository.getMovieDetail(testMovieId) } returns Result.success(testMovieDetail)

        // When
        val result = getMovieDetailUseCase(testMovieId)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(testMovieDetail)
        coVerify(exactly = 1) { movieRepository.getMovieDetail(testMovieId) }
    }

    @Test
    fun `when repository returns failure, use case should return failure`() = runTest {
        // Given
        val exception = Exception("Movie not found")
        coEvery { movieRepository.getMovieDetail(testMovieId) } returns Result.failure(exception)

        // When
        val result = getMovieDetailUseCase(testMovieId)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
        coVerify(exactly = 1) { movieRepository.getMovieDetail(testMovieId) }
    }

    @Test
    fun `invoke function should work correctly`() = runTest {
        // Given
        coEvery { movieRepository.getMovieDetail(testMovieId) } returns Result.success(testMovieDetail)

        // When - using invoke operator
        val result = getMovieDetailUseCase.invoke(testMovieId)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(testMovieDetail)
        coVerify(exactly = 1) { movieRepository.getMovieDetail(testMovieId) }
    }
}
