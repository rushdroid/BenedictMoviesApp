package com.rushdroid.benedictmovies.presentation.ui.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.rushdroid.benedictmovies.R
import com.rushdroid.benedictmovies.core.util.DateUtil
import com.rushdroid.benedictmovies.core.util.FormatUtil
import com.rushdroid.benedictmovies.core.util.ImageUtil
import com.rushdroid.benedictmovies.databinding.ItemMovieListBinding
import com.rushdroid.benedictmovies.domain.model.Movie

/**
 * ViewHolder for list layout displaying movies in single-column horizontal format.
 */
class ListMovieViewHolder(
    private val binding: ItemMovieListBinding,
    private val onMovieClick: (Movie) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: Movie) {
        binding.apply {
            textViewTitle.text = movie.title
            textViewReleaseDate.text = DateUtil.extractYear(movie.releaseDate, binding.root.context)
            textViewRating.text = FormatUtil.formatRating(movie.voteAverage)

            imageViewPoster.load(ImageUtil.getPosterUrl(movie.posterPath)) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                error(R.drawable.ic_launcher_foreground)
                transformations(RoundedCornersTransformation(8f))
            }

            root.setOnClickListener {
                onMovieClick(movie)
            }
        }
    }
}
