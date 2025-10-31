package com.rushdroid.benedictmovies.data.mapper

import com.google.common.truth.Truth.assertThat
import com.rushdroid.benedictmovies.data.remote.dto.GenreDto
import com.rushdroid.benedictmovies.data.remote.dto.MovieDto
import org.junit.Test

class MovieMapperTest {

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

    @Test
    fun `toMovie should map MovieDto to Movie correctly`() {
        // When
        val movie = testMovieDto.toMovie()

        // Then
        assertThat(movie.id).isEqualTo(123)
        assertThat(movie.title).isEqualTo("Doctor Strange")
        assertThat(movie.overview).isEqualTo("A former neurosurgeon embarks on a journey of healing...")
        assertThat(movie.posterPath).isEqualTo("/poster.jpg")
        assertThat(movie.backdropPath).isEqualTo("/backdrop.jpg")
        assertThat(movie.releaseDate).isEqualTo("2016-10-25")
        assertThat(movie.voteAverage).isEqualTo(7.5)
        assertThat(movie.voteCount).isEqualTo(15000)
        assertThat(movie.popularity).isEqualTo(85.0)
    }

    @Test
    fun `toMovie should handle null poster and backdrop paths`() {
        // Given
        val movieDtoWithNulls = testMovieDto.copy(posterPath = null, backdropPath = null)

        // When
        val movie = movieDtoWithNulls.toMovie()

        // Then
        assertThat(movie.posterPath).isEqualTo("")
        assertThat(movie.backdropPath).isEqualTo("")
    }

    @Test
    fun `toMovieDetail should map MovieDto to MovieDetail correctly`() {
        // When
        val movieDetail = testMovieDto.toMovieDetail()

        // Then
        assertThat(movieDetail.id).isEqualTo(123)
        assertThat(movieDetail.title).isEqualTo("Doctor Strange")
        assertThat(movieDetail.overview).isEqualTo("A former neurosurgeon embarks on a journey of healing...")
        assertThat(movieDetail.posterPath).isEqualTo("/poster.jpg")
        assertThat(movieDetail.backdropPath).isEqualTo("/backdrop.jpg")
        assertThat(movieDetail.releaseDate).isEqualTo("2016-10-25")
        assertThat(movieDetail.voteAverage).isEqualTo(7.5)
        assertThat(movieDetail.voteCount).isEqualTo(15000)
        assertThat(movieDetail.runtime).isEqualTo(115)
        assertThat(movieDetail.budget).isEqualTo(165000000)
        assertThat(movieDetail.revenue).isEqualTo(677718395)
        assertThat(movieDetail.tagline).isEqualTo("The impossibilities are endless")
        assertThat(movieDetail.genres).hasSize(2)
        assertThat(movieDetail.genres.first().name).isEqualTo("Fantasy")
        assertThat(movieDetail.genres.last().name).isEqualTo("Action")
    }

    @Test
    fun `toMovieDetail should handle null values correctly`() {
        // Given
        val movieDtoWithNulls = testMovieDto.copy(
            runtime = null,
            budget = null,
            revenue = null,
            tagline = null,
            posterPath = null,
            backdropPath = null,
            genres = emptyList()
        )

        // When
        val movieDetail = movieDtoWithNulls.toMovieDetail()

        // Then
        assertThat(movieDetail.runtime).isNull()
        assertThat(movieDetail.budget).isEqualTo(0)
        assertThat(movieDetail.revenue).isEqualTo(0)
        assertThat(movieDetail.tagline).isEqualTo("")
        assertThat(movieDetail.posterPath).isEqualTo("")
        assertThat(movieDetail.backdropPath).isEqualTo("")
        assertThat(movieDetail.genres).hasSize(0)
    }

    @Test
    fun `toGenre should map GenreDto to Genre correctly`() {
        // Given
        val genreDto = GenreDto(id = 14, name = "Fantasy")

        // When
        val genre = genreDto.toGenre()

        // Then
        assertThat(genre.id).isEqualTo(14)
        assertThat(genre.name).isEqualTo("Fantasy")
    }
}
