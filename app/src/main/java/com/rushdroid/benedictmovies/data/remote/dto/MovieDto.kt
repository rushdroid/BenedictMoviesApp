package com.rushdroid.benedictmovies.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("runtime")
    val runtime: Int? = null,
    @SerializedName("genres")
    val genres: List<GenreDto>? = null,
    @SerializedName("tagline")
    val tagline: String? = null,
    @SerializedName("budget")
    val budget: Long? = null,
    @SerializedName("revenue")
    val revenue: Long? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("homepage")
    val homepage: String? = null
)

data class GenreDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)