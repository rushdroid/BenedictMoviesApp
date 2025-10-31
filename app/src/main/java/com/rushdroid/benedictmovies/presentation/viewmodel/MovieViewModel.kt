package com.rushdroid.benedictmovies.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rushdroid.benedictmovies.core.constants.Constants
import com.rushdroid.benedictmovies.domain.model.Movie
import com.rushdroid.benedictmovies.domain.model.MovieDetail
import com.rushdroid.benedictmovies.domain.usecase.GetMovieDetailUseCase
import com.rushdroid.benedictmovies.domain.usecase.GetMoviesUseCase
import com.rushdroid.benedictmovies.domain.usecase.GetSimilarMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing movie data and UI state.
 * Handles both movie list and movie detail operations.
 */
@HiltViewModel
class MovieViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val getSimilarMoviesUseCase: GetSimilarMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    init {
        loadBenedictCumberbatchMovies()
    }

    /**
     * Loads Benedict Cumberbatch's filmography from the API.
     */
    fun loadBenedictCumberbatchMovies() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            getMoviesUseCase(Constants.BENEDICT_CUMBERBATCH_ID)
                .onSuccess { movies ->
                    _uiState.value = _uiState.value.copy(
                        movies = movies,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }

    /**
     * Fetches detailed information for a specific movie.
     *
     * @param movieId The TMDB movie ID
     */
    fun loadMovieDetail(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingDetail = true, detailError = null)

            getMovieDetailUseCase(movieId)
                .onSuccess { movieDetail ->
                    _uiState.value = _uiState.value.copy(
                        selectedMovieDetail = movieDetail,
                        isLoadingDetail = false
                    )
                    // Load similar movies after movie detail is loaded
                    loadSimilarMovies(movieId)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingDetail = false,
                        detailError = exception.message
                    )
                }
        }
    }

    /**
     * Fetches similar movies for a specific movie.
     *
     * @param movieId The TMDB movie ID
     */
    fun loadSimilarMovies(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingSimilar = true, similarError = null)

            getSimilarMoviesUseCase(movieId)
                .onSuccess { movies ->
                    _uiState.value = _uiState.value.copy(
                        similarMovies = movies,
                        isLoadingSimilar = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingSimilar = false,
                        similarError = exception.message
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null, detailError = null, similarError = null)
    }
}

/**
 * UI state holder for movie-related screens.
 */
data class MovieUiState(
    val movies: List<Movie> = emptyList(),
    val selectedMovieDetail: MovieDetail? = null,
    val similarMovies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingDetail: Boolean = false,
    val isLoadingSimilar: Boolean = false,
    val error: String? = null,
    val detailError: String? = null,
    val similarError: String? = null
)
