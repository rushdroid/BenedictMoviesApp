package com.rushdroid.benedictmovies.presentation.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rushdroid.benedictmovies.databinding.ItemMovieBinding
import com.rushdroid.benedictmovies.databinding.ItemMovieListBinding
import com.rushdroid.benedictmovies.domain.model.Movie
import com.rushdroid.benedictmovies.presentation.ui.adapter.viewholder.GridMovieViewHolder
import com.rushdroid.benedictmovies.presentation.ui.adapter.viewholder.ListMovieViewHolder

/**
 * RecyclerView adapter supporting both grid and list layout modes for movies.
 */
class MovieAdapter(
    private val onMovieClick: (Movie) -> Unit
) : ListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_GRID = 0
        private const val VIEW_TYPE_LIST = 1
    }

    private var isGridLayout = false

    @SuppressLint("NotifyDataSetChanged")
    fun setLayoutType(isGrid: Boolean) {
        if (isGridLayout != isGrid) {
            isGridLayout = isGrid
            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isGridLayout) VIEW_TYPE_GRID else VIEW_TYPE_LIST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_GRID -> {
                val binding = ItemMovieBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                GridMovieViewHolder(binding, onMovieClick)
            }
            VIEW_TYPE_LIST -> {
                val binding = ItemMovieListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ListMovieViewHolder(binding, onMovieClick)
            }
            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie = getItem(position)
        when (holder) {
            is GridMovieViewHolder -> holder.bind(movie)
            is ListMovieViewHolder -> holder.bind(movie)
        }
    }
}
