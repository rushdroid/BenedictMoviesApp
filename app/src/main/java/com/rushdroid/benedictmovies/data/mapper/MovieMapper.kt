package com.rushdroid.benedictmovies.data.mapper

import com.rushdroid.benedictmovies.data.remote.dto.MovieDto
import com.rushdroid.benedictmovies.data.remote.dto.GenreDto
import com.rushdroid.benedictmovies.domain.model.Genre
import com.rushdroid.benedictmovies.domain.model.Movie
import com.rushdroid.benedictmovies.domain.model.MovieDetail

/**
 * Mapper functions for converting DTOs to domain models.
 */

/**
 * Converts MovieDto to Movie domain model.
 */
fun MovieDto.toMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        posterPath = posterPath.orEmpty(),
        backdropPath = backdropPath.orEmpty(),
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity
    )
}

/**
 * Converts MovieDto to MovieDetail domain model.
 * Used when MovieDto contains full detail information from the detail endpoint.
 */
fun MovieDto.toMovieDetail(): MovieDetail {
    return MovieDetail(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        posterPath = posterPath.orEmpty(),
        backdropPath = backdropPath.orEmpty(),
        voteAverage = voteAverage,
        voteCount = voteCount,
        runtime = runtime,
        budget = budget ?: 0,
        revenue = revenue ?: 0,
        tagline = tagline.orEmpty(),
        popularity = popularity,
        status = status.orEmpty(),
        homepage = homepage,
        genres = genres?.map { it.toGenre() }.orEmpty()
    )
}

/**
 * Converts GenreDto to Genre domain model.
 */
fun GenreDto.toGenre(): Genre {
    return Genre(
        id = id,
        name = name
    )
}
