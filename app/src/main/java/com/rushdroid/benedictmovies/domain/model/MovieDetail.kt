package com.rushdroid.benedictmovies.domain.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val runtime: Int?,
    val genres: List<Genre>,
    val tagline: String?,
    val budget: Long,
    val revenue: Long,
    val status: String,
    val homepage: String?
)