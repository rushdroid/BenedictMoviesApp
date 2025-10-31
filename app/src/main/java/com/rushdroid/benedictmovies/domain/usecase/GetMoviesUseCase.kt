package com.rushdroid.benedictmovies.domain.usecase

import com.rushdroid.benedictmovies.domain.model.Movie
import com.rushdroid.benedictmovies.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * Use case for fetching movies associated with a specific person.
 */
class GetMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(personId: Int): Result<List<Movie>> {
        return repository.getMoviesByPerson(personId)
    }
}
