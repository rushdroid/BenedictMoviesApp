package com.rushdroid.benedictmovies.data.remote.api

import com.rushdroid.benedictmovies.data.remote.dto.MovieDto
import com.rushdroid.benedictmovies.data.remote.dto.MovieResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service interface for TMDB API endpoints.
 */
interface MovieApiService {

    /**
     * Fetches movies associated with a specific person (actor/director).
     *
     * @param personId The TMDB person ID
     * @return Response containing list of movies
     */
    @GET("discover/movie")
    suspend fun getMoviesByPerson(
        @Query("with_people") personId: Int
    ): MovieResponseDto

    /**
     * Fetches detailed information about a specific movie.
     *
     * @param movieId The TMDB movie ID
     * @return Movie detail response
     */
    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: Int
    ): MovieDto

    /**
     * Fetches similar movies for a specific movie.
     *
     * @param movieId The TMDB movie ID
     * @return Response containing list of similar movies
     */
    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovies(
        @Path("movie_id") movieId: Int
    ): MovieResponseDto
}
