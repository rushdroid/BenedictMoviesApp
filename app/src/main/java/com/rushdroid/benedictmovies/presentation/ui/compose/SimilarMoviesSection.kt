package com.rushdroid.benedictmovies.presentation.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rushdroid.benedictmovies.core.util.ImageUtil
import com.rushdroid.benedictmovies.domain.model.Movie
import com.rushdroid.benedictmovies.presentation.theme.BenedictMoviesTheme

/**
 * Composable that displays a horizontal list of similar movies.
 *
 * @param similarMovies List of similar movies to display
 * @param isLoading Whether similar movies are currently being loaded
 * @param onMovieClick Callback when a movie is clicked
 * @param modifier Modifier for styling
 */
@Composable
fun SimilarMoviesSection(
    similarMovies: List<Movie>,
    isLoading: Boolean,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("similar_movies_section")
    ) {
        Text(
            text = "Similar Movies",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .testTag("similar_movies_loading"),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            similarMovies.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .testTag("similar_movies_empty"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No similar movies found",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }

            else -> {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("similar_movies_list"),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = similarMovies,
                        key = { it.id }
                    ) { movie ->
                        SimilarMovieCard(
                            movie = movie,
                            onClick = { onMovieClick(movie) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card displaying a single similar movie with poster and title.
 */
@Composable
private fun SimilarMovieCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(120.dp)
            .clickable(onClick = onClick)
            .testTag("similar_movie_card_${movie.id}"),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Movie Poster
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(ImageUtil.getPosterUrl(movie.posterPath))
                    .crossfade(true)
                    .build(),
                contentDescription = "Poster for ${movie.title}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            // Movie Title
            Text(
                text = movie.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}

// Preview functions
@Preview(name = "Similar Movies Section - With Movies (Light)")
@Composable
private fun SimilarMoviesSectionLightPreview() {
    BenedictMoviesTheme(darkTheme = false) {
        SimilarMoviesSection(
            similarMovies = createPreviewMovies(),
            isLoading = false,
            onMovieClick = {},
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Preview(name = "Similar Movies Section - With Movies (Dark)")
@Composable
private fun SimilarMoviesSectionDarkPreview() {
    BenedictMoviesTheme(darkTheme = true) {
        SimilarMoviesSection(
            similarMovies = createPreviewMovies(),
            isLoading = false,
            onMovieClick = {},
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Preview(name = "Similar Movies Section - Loading")
@Composable
private fun SimilarMoviesSectionLoadingPreview() {
    BenedictMoviesTheme(darkTheme = false) {
        SimilarMoviesSection(
            similarMovies = emptyList(),
            isLoading = true,
            onMovieClick = {},
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Preview(name = "Similar Movies Section - Empty")
@Composable
private fun SimilarMoviesSectionEmptyPreview() {
    BenedictMoviesTheme(darkTheme = false) {
        SimilarMoviesSection(
            similarMovies = emptyList(),
            isLoading = false,
            onMovieClick = {},
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Preview(name = "Similar Movie Card")
@Composable
private fun SimilarMovieCardPreview() {
    BenedictMoviesTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            SimilarMovieCard(
                movie = Movie(
                    id = 1,
                    title = "Doctor Strange in the Multiverse of Madness",
                    overview = "A great movie",
                    posterPath = "/poster.jpg",
                    backdropPath = "/backdrop.jpg",
                    releaseDate = "2022-05-06",
                    voteAverage = 7.5,
                    voteCount = 5000,
                    popularity = 150.0
                ),
                onClick = {}
            )
        }
    }
}

// Helper function to create sample movies for previews
private fun createPreviewMovies() = listOf(
    Movie(
        id = 1,
        title = "Doctor Strange in the Multiverse of Madness",
        overview = "A journey through the multiverse",
        posterPath = "/poster1.jpg",
        backdropPath = "/backdrop1.jpg",
        releaseDate = "2022-05-06",
        voteAverage = 7.5,
        voteCount = 5000,
        popularity = 150.0
    ),
    Movie(
        id = 2,
        title = "The Avengers",
        overview = "Earth's mightiest heroes",
        posterPath = "/poster2.jpg",
        backdropPath = "/backdrop2.jpg",
        releaseDate = "2012-04-25",
        voteAverage = 8.0,
        voteCount = 25000,
        popularity = 200.0
    ),
    Movie(
        id = 3,
        title = "Thor: Ragnarok",
        overview = "Thor must escape from Sakaar",
        posterPath = "/poster3.jpg",
        backdropPath = "/backdrop3.jpg",
        releaseDate = "2017-10-25",
        voteAverage = 7.9,
        voteCount = 18000,
        popularity = 180.0
    ),
    Movie(
        id = 4,
        title = "Spider-Man: No Way Home",
        overview = "The multiverse unleashed",
        posterPath = "/poster4.jpg",
        backdropPath = "/backdrop4.jpg",
        releaseDate = "2021-12-15",
        voteAverage = 8.5,
        voteCount = 30000,
        popularity = 250.0
    ),
    Movie(
        id = 5,
        title = "Avengers: Endgame",
        overview = "The final battle",
        posterPath = "/poster5.jpg",
        backdropPath = "/backdrop5.jpg",
        releaseDate = "2019-04-24",
        voteAverage = 8.4,
        voteCount = 28000,
        popularity = 240.0
    )
)

