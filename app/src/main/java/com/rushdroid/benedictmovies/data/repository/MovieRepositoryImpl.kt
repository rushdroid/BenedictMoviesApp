package com.rushdroid.benedictmovies.data.repository

import com.rushdroid.benedictmovies.core.util.ErrorHandler
import com.rushdroid.benedictmovies.core.util.StringResourceProvider
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
        return try {
            val response = apiService.getMoviesByPerson(personId)
            val movies = response.results.map { it.toMovie() }
            Result.success(movies)
        } catch (e: Exception) {
            Result.failure(Exception(ErrorHandler.getErrorMessage(stringResourceProvider, e), e))
        }
    }

    override suspend fun getMovieDetail(movieId: Int): Result<MovieDetail> {
        return try {
            val response = apiService.getMovieDetail(movieId)
            val movieDetail = response.toMovieDetail()
            Result.success(movieDetail)
        } catch (e: Exception) {
            Result.failure(Exception(ErrorHandler.getErrorMessage(stringResourceProvider, e), e))
        }
    }

    override suspend fun getSimilarMovies(movieId: Int): Result<List<Movie>> {
        return try {
            val response = apiService.getSimilarMovies(movieId)
            val movies = response.results.map { it.toMovie() }
            Result.success(movies)
        } catch (e: Exception) {
            Result.failure(Exception(ErrorHandler.getErrorMessage(stringResourceProvider, e), e))
        }
    }
}
