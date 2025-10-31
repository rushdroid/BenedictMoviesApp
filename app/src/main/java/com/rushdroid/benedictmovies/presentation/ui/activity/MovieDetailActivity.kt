package com.rushdroid.benedictmovies.presentation.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.rushdroid.benedictmovies.presentation.theme.BenedictMoviesTheme
import com.rushdroid.benedictmovies.presentation.ui.compose.MovieDetailScreen
import com.rushdroid.benedictmovies.presentation.viewmodel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailActivity : ComponentActivity() {

    private val viewModel: MovieViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val movieId = intent.getIntExtra(EXTRA_MOVIE_ID, -1)
        val movieTitle = intent.getStringExtra(EXTRA_MOVIE_TITLE) ?: "Movie Detail"

        if (movieId != -1) {
            viewModel.loadMovieDetail(movieId)
        }

        setContent {
            BenedictMoviesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MovieDetailScreen(
                        viewModel = viewModel,
                        movieTitle = movieTitle,
                        movieId = movieId,
                        onBackPressed = { finish() },
                        onSimilarMovieClick = { movie ->
                            // Load the selected similar movie's details
                            viewModel.loadMovieDetail(movie.id)
                        }
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_MOVIE_ID = "extra_movie_id"
        const val EXTRA_MOVIE_TITLE = "extra_movie_title"
    }
}
