package com.rushdroid.benedictmovies.presentation.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.rushdroid.benedictmovies.domain.model.Movie

/**
 * DiffUtil callback for efficient RecyclerView updates when movie list changes.
 */
class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {

    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }
}
