package com.rushdroid.benedictmovies.domain.usecase

import com.rushdroid.benedictmovies.domain.model.MovieDetail
import com.rushdroid.benedictmovies.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * Use case for fetching detailed information about a specific movie.
 */
class GetMovieDetailUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    /**
     * Invokes the use case to fetch movie details.
     *
     * @param movieId The ID of the movie to fetch details for.
     * @return A [Result] containing the [MovieDetail] if successful, or an error otherwise.
     */
    suspend operator fun invoke(movieId: Int): Result<MovieDetail> {
        return repository.getMovieDetail(movieId)
    }
}