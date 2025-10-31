package com.rushdroid.benedictmovies.domain.repository

import com.rushdroid.benedictmovies.domain.model.Movie
import com.rushdroid.benedictmovies.domain.model.MovieDetail

interface MovieRepository {

    suspend fun getMoviesByPerson(personId: Int): Result<List<Movie>>

    suspend fun getMovieDetail(movieId: Int): Result<MovieDetail>

    suspend fun getSimilarMovies(movieId: Int): Result<List<Movie>>
}