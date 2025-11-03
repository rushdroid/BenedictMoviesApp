package com.rushdroid.benedictmovies.data.repository

import com.rushdroid.benedictmovies.R
import com.rushdroid.benedictmovies.core.util.StringResourceProvider
import com.rushdroid.benedictmovies.core.util.safeApiCall
import com.rushdroid.benedictmovies.data.remote.api.MovieApiService
import com.rushdroid.benedictmovies.data.mapper.toMovie
import com.rushdroid.benedictmovies.data.mapper.toMovieDetail
import com.rushdroid.benedictmovies.domain.model.Movie
import com.rushdroid.benedictmovies.domain.model.MovieDetail
import com.rushdroid.benedictmovies.domain.repository.MovieRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of MovieRepository that fetches data from TMDB API.
 */
@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val apiService: MovieApiService,
    private val stringResourceProvider: StringResourceProvider
) : MovieRepository {

    override suspend fun getMoviesByPerson(personId: Int): Result<List<Movie>> {
        return safeApiCall(
            stringResourceProvider = stringResourceProvider,
            validateNotEmpty = true
        ) {
            val response = apiService.getMoviesByPerson(personId)
            response.results.map { it.toMovie() }
        }
    }

    override suspend fun getMovieDetail(movieId: Int): Result<MovieDetail> {
        return safeApiCall(stringResourceProvider) {
            val response = apiService.getMovieDetail(movieId)
            response.toMovieDetail()
        }
    }

    override suspend fun getSimilarMovies(movieId: Int): Result<List<Movie>> {
        return safeApiCall(
            stringResourceProvider = stringResourceProvider,
            validateNotEmpty = true,
            emptyMessage = stringResourceProvider.getString(R.string.error_no_similar_movies)
        ) {
            val response = apiService.getSimilarMovies(movieId)
            response.results.map { it.toMovie() }
        }
    }
}
