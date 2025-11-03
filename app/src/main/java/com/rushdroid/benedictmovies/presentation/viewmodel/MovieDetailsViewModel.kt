package com.rushdroid.benedictmovies.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rushdroid.benedictmovies.domain.model.Movie
import com.rushdroid.benedictmovies.domain.model.MovieDetail
import com.rushdroid.benedictmovies.domain.usecase.GetMovieDetailUseCase
import com.rushdroid.benedictmovies.domain.usecase.GetSimilarMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing movie detail data and UI state.
 * Handles movie detail and similar movies operations.
 */
@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val getSimilarMoviesUseCase: GetSimilarMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailsUiState())
    val uiState: StateFlow<MovieDetailsUiState> = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = MovieDetailsUiState()
        )

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
        _uiState.value = _uiState.value.copy(detailError = null, similarError = null)
    }
}

/**
 * UI state holder for movie details screen.
 */
data class MovieDetailsUiState(
    val selectedMovieDetail: MovieDetail? = null,
    val similarMovies: List<Movie> = emptyList(),
    val isLoadingDetail: Boolean = false,
    val isLoadingSimilar: Boolean = false,
    val detailError: String? = null,
    val similarError: String? = null
)

