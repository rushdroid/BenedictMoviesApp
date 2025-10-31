package com.rushdroid.benedictmovies.presentation.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rushdroid.benedictmovies.R
import com.rushdroid.benedictmovies.core.util.CurrencyUtil
import com.rushdroid.benedictmovies.core.util.FormatUtil
import com.rushdroid.benedictmovies.core.util.ImageUtil
import com.rushdroid.benedictmovies.domain.model.Genre
import com.rushdroid.benedictmovies.domain.model.Movie
import com.rushdroid.benedictmovies.domain.model.MovieDetail
import com.rushdroid.benedictmovies.presentation.theme.BenedictMoviesTheme
import com.rushdroid.benedictmovies.presentation.viewmodel.MovieViewModel

/**
 * Compose screen displaying detailed information about a selected movie.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    viewModel: MovieViewModel,
    movieTitle: String,
    movieId: Int,
    onBackPressed: () -> Unit,
    onSimilarMovieClick: (Movie) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = movieTitle,
                        maxLines = 1,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPressed,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->

        val movieDetail = uiState.selectedMovieDetail

        when {
            uiState.isLoadingDetail -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .testTag("loading_indicator"),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            uiState.detailError != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${uiState.detailError}",
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(
                            onClick = {
                                viewModel.clearError()
                                viewModel.loadMovieDetail(movieId)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(text = stringResource(R.string.retry_button), color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }

            movieDetail != null -> {
                MovieDetailContent(
                    movieDetail = movieDetail,
                    similarMovies = uiState.similarMovies,
                    isLoadingSimilar = uiState.isLoadingSimilar,
                    onSimilarMovieClick = onSimilarMovieClick,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun MovieDetailContent(
    movieDetail: MovieDetail,
    similarMovies: List<Movie>,
    isLoadingSimilar: Boolean,
    onSimilarMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .testTag("movie_detail_content")
    ) {
        // Backdrop Image with Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(ImageUtil.getBackdropUrl(movieDetail.backdropPath))
                    .crossfade(true)
                    .build(),
                contentDescription = "Movie backdrop",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            // Movie title and rating overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = movieDetail.title,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color.Yellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = FormatUtil.formatRating(movieDetail.voteAverage),
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Text(
                        text = " (${movieDetail.voteCount} votes)",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Movie Information
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Release Date and Runtime
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Release Date: ${movieDetail.releaseDate}",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp
                )
                movieDetail.runtime?.let { runtime ->
                    Text(
                        text = "${runtime} min",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Genres
            if (movieDetail.genres.isNotEmpty()) {
                Text(
                    text = "Genres",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("genres_section")
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    movieDetail.genres.forEach { genre ->
                        Card(
                            modifier = Modifier.testTag("genre_chip"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = genre.name,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Tagline
            movieDetail.tagline?.takeIf { it.isNotBlank() }?.let { tagline ->
                Text(
                    text = "\"$tagline\"",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Overview
            Text(
                text = "Overview",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = movieDetail.overview,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Additional Details
            if (movieDetail.budget > 0 || movieDetail.revenue > 0) {
                Text(
                    text = "Box Office",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                if (movieDetail.budget > 0) {
                    Text(
                        text = "Budget: $${CurrencyUtil.formatCurrency(movieDetail.budget)}",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (movieDetail.revenue > 0) {
                    Text(
                        text = "Revenue: $${CurrencyUtil.formatCurrency(movieDetail.revenue)}",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Similar Movies Section
            SimilarMoviesSection(
                similarMovies = similarMovies,
                isLoading = isLoadingSimilar,
                onMovieClick = onSimilarMovieClick,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}


// Preview functions for development
@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Movie Detail Screen - Light")
@Composable
private fun MovieDetailScreenLightPreview() {
    BenedictMoviesTheme(darkTheme = false) {
        // Mock the screen content since we can't easily mock the ViewModel in previews
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Doctor Strange",
                            maxLines = 1,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back button"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
            MovieDetailContent(
                movieDetail = createPreviewMovieDetail(),
                similarMovies = emptyList(),
                isLoadingSimilar = false,
                onSimilarMovieClick = {},
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Movie Detail Screen - Dark")
@Composable
private fun MovieDetailScreenDarkPreview() {
    BenedictMoviesTheme(darkTheme = true) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Doctor Strange",
                            maxLines = 1,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back button"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
            MovieDetailContent(
                movieDetail = createPreviewMovieDetail(),
                similarMovies = emptyList(),
                isLoadingSimilar = false,
                onSimilarMovieClick = {},
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

// Helper function to create sample movie detail for previews
private fun createPreviewMovieDetail() = MovieDetail(
    id = 123,
    title = "Doctor Strange",
    overview = "A former neurosurgeon embarks on a journey of healing only to be drawn into the world of the mystic arts.",
    posterPath = "/poster.jpg",
    backdropPath = "/backdrop.jpg",
    releaseDate = "2016-10-25",
    voteAverage = 7.5,
    voteCount = 15000,
    runtime = 115,
    budget = 165000000,
    revenue = 677718395,
    tagline = "The impossibilities are endless",
    genres = listOf(
        Genre(id = 14, name = "Fantasy"),
        Genre(id = 28, name = "Action"),
        Genre(id = 12, name = "Adventure")
    ),
    popularity = 150.0,
    status = "Released",
    homepage = "https://marvel.com"
)
