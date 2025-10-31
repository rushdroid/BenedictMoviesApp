package com.rushdroid.benedictmovies.data.repository

import com.google.common.truth.Truth.assertThat
import com.rushdroid.benedictmovies.core.util.StringResourceProvider
import com.rushdroid.benedictmovies.data.remote.api.MovieApiService
import com.rushdroid.benedictmovies.data.remote.dto.GenreDto
import com.rushdroid.benedictmovies.data.remote.dto.MovieDto
import com.rushdroid.benedictmovies.data.remote.dto.MovieResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.UnknownHostException

class MovieRepositoryImplTest {

    private lateinit var apiService: MovieApiService
    private lateinit var stringResourceProvider: StringResourceProvider
    private lateinit var repository: MovieRepositoryImpl

    private val testPersonId = 71580
    private val testMovieId = 123

    private val testMovieDto = MovieDto(
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
        genres = listOf(
            GenreDto(id = 14, name = "Fantasy"),
            GenreDto(id = 28, name = "Action")
        ),
        tagline = "The impossibilities are endless",
        budget = 165000000,
        revenue = 677718395,
        status = "Released",
        homepage = "https://www.marvel.com/movies/doctor-strange"
    )

    private val testMovieResponseDto = MovieResponseDto(
        page = 1,
        results = listOf(testMovieDto),
        totalPages = 1,
        totalResults = 1
    )

    @Before
    fun setup() {
        apiService = mockk()
        stringResourceProvider = mockk()
        repository = MovieRepositoryImpl(apiService, stringResourceProvider)
    }

    @Test
    fun `getMoviesByPerson should return success when API call succeeds`() = runTest {
        // Given
        coEvery { apiService.getMoviesByPerson(testPersonId) } returns testMovieResponseDto

        // When
        val result = repository.getMoviesByPerson(testPersonId)

        // Then
        assertThat(result.isSuccess).isTrue()
        val movies = result.getOrNull()!!
        assertThat(movies).hasSize(1)
        assertThat(movies.first().title).isEqualTo("Doctor Strange")
        assertThat(movies.first().id).isEqualTo(123)
        coVerify(exactly = 1) { apiService.getMoviesByPerson(testPersonId) }
    }

    @Test
    fun `getMoviesByPerson should return failure when API throws UnknownHostException`() = runTest {
        // Given
        val exception = UnknownHostException("No internet connection")
        coEvery { apiService.getMoviesByPerson(testPersonId) } throws exception
        every { stringResourceProvider.getString(any()) } returns "No internet connection available"

        // When
        val result = repository.getMoviesByPerson(testPersonId)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("No internet connection available")
    }

    @Test
    fun `getMoviesByPerson should return failure when API throws IOException`() = runTest {
        // Given
        val exception = IOException("Network error")
        coEvery { apiService.getMoviesByPerson(testPersonId) } throws exception
        every { stringResourceProvider.getString(any()) } returns "Network error occurred"

        // When
        val result = repository.getMoviesByPerson(testPersonId)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Network error occurred")
    }

    @Test
    fun `getMovieDetail should return success when API call succeeds`() = runTest {
        // Given
        coEvery { apiService.getMovieDetail(testMovieId) } returns testMovieDto

        // When
        val result = repository.getMovieDetail(testMovieId)

        // Then
        assertThat(result.isSuccess).isTrue()
        val movieDetail = result.getOrNull()!!
        assertThat(movieDetail.title).isEqualTo("Doctor Strange")
        assertThat(movieDetail.id).isEqualTo(123)
        assertThat(movieDetail.runtime).isEqualTo(115)
        assertThat(movieDetail.genres).hasSize(2)
        assertThat(movieDetail.genres.first().name).isEqualTo("Fantasy")
        coVerify(exactly = 1) { apiService.getMovieDetail(testMovieId) }
    }

    @Test
    fun `getMovieDetail should return failure when API throws HttpException`() = runTest {
        // Given
        val httpException = HttpException(Response.error<Any>(404, mockk(relaxed = true)))
        coEvery { apiService.getMovieDetail(testMovieId) } throws httpException
        every { stringResourceProvider.getString(any()) } returns "Resource not found"

        // When
        val result = repository.getMovieDetail(testMovieId)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Resource not found")
    }

    // Similar Movies Tests
    @Test
    fun `getSimilarMovies should return success when API call succeeds`() = runTest {
        // Given
        val similarMoviesDto = listOf(
            testMovieDto.copy(id = 456, title = "Doctor Strange in the Multiverse of Madness"),
            testMovieDto.copy(id = 789, title = "Thor: Ragnarok"),
            testMovieDto.copy(id = 321, title = "The Avengers")
        )
        val similarMoviesResponse = MovieResponseDto(
            page = 1,
            results = similarMoviesDto,
            totalPages = 1,
            totalResults = 3
        )
        coEvery { apiService.getSimilarMovies(testMovieId) } returns similarMoviesResponse

        // When
        val result = repository.getSimilarMovies(testMovieId)

        // Then
        assertThat(result.isSuccess).isTrue()
        val movies = result.getOrNull()!!
        assertThat(movies).hasSize(3)
        assertThat(movies[0].title).isEqualTo("Doctor Strange in the Multiverse of Madness")
        assertThat(movies[1].title).isEqualTo("Thor: Ragnarok")
        assertThat(movies[2].title).isEqualTo("The Avengers")
        coVerify(exactly = 1) { apiService.getSimilarMovies(testMovieId) }
    }

    @Test
    fun `getSimilarMovies should return empty list when no similar movies found`() = runTest {
        // Given
        val emptyResponse = MovieResponseDto(
            page = 1,
            results = emptyList(),
            totalPages = 1,
            totalResults = 0
        )
        coEvery { apiService.getSimilarMovies(testMovieId) } returns emptyResponse

        // When
        val result = repository.getSimilarMovies(testMovieId)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEmpty()
        coVerify(exactly = 1) { apiService.getSimilarMovies(testMovieId) }
    }

    @Test
    fun `getSimilarMovies should return failure when API throws UnknownHostException`() = runTest {
        // Given
        val exception = UnknownHostException("No internet connection")
        coEvery { apiService.getSimilarMovies(testMovieId) } throws exception
        every { stringResourceProvider.getString(any()) } returns "No internet connection available"

        // When
        val result = repository.getSimilarMovies(testMovieId)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("No internet connection available")
        coVerify(exactly = 1) { apiService.getSimilarMovies(testMovieId) }
    }

    @Test
    fun `getSimilarMovies should return failure when API throws IOException`() = runTest {
        // Given
        val exception = IOException("Network error")
        coEvery { apiService.getSimilarMovies(testMovieId) } throws exception
        every { stringResourceProvider.getString(any()) } returns "Network error occurred"

        // When
        val result = repository.getSimilarMovies(testMovieId)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Network error occurred")
        coVerify(exactly = 1) { apiService.getSimilarMovies(testMovieId) }
    }

    @Test
    fun `getSimilarMovies should return failure when API throws HttpException`() = runTest {
        // Given
        val httpException = HttpException(Response.error<Any>(404, mockk(relaxed = true)))
        coEvery { apiService.getSimilarMovies(testMovieId) } throws httpException
        every { stringResourceProvider.getString(any()) } returns "Resource not found"

        // When
        val result = repository.getSimilarMovies(testMovieId)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Resource not found")
        coVerify(exactly = 1) { apiService.getSimilarMovies(testMovieId) }
    }

    @Test
    fun `getSimilarMovies should map API response correctly to domain models`() = runTest {
        // Given
        val similarMoviesDto = listOf(
            testMovieDto.copy(
                id = 456,
                title = "Similar Movie",
                voteAverage = 8.5,
                voteCount = 10000,
                popularity = 120.0
            )
        )
        val similarMoviesResponse = MovieResponseDto(
            page = 1,
            results = similarMoviesDto,
            totalPages = 1,
            totalResults = 1
        )
        coEvery { apiService.getSimilarMovies(testMovieId) } returns similarMoviesResponse

        // When
        val result = repository.getSimilarMovies(testMovieId)

        // Then
        assertThat(result.isSuccess).isTrue()
        val movie = result.getOrNull()!!.first()
        assertThat(movie.id).isEqualTo(456)
        assertThat(movie.title).isEqualTo("Similar Movie")
        assertThat(movie.voteAverage).isEqualTo(8.5)
        assertThat(movie.voteCount).isEqualTo(10000)
        assertThat(movie.popularity).isEqualTo(120.0)
        coVerify(exactly = 1) { apiService.getSimilarMovies(testMovieId) }
    }
}
