package com.rushdroid.benedictmovies.domain.usecase

import com.rushdroid.benedictmovies.domain.model.Movie
import com.rushdroid.benedictmovies.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * Use case for fetching similar movies for a specific movie.
 */
class GetSimilarMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Result<List<Movie>> {
        return repository.getSimilarMovies(movieId)
    }
}

