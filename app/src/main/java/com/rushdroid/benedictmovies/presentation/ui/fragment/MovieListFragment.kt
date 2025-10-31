package com.rushdroid.benedictmovies.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.rushdroid.benedictmovies.R
import com.rushdroid.benedictmovies.databinding.FragmentMovieListBinding
import com.rushdroid.benedictmovies.presentation.ui.activity.MovieDetailActivity
import com.rushdroid.benedictmovies.presentation.ui.adapter.MovieAdapter
import com.rushdroid.benedictmovies.presentation.viewmodel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieListFragment : Fragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieViewModel by viewModels()
    private lateinit var movieAdapter: MovieAdapter
    private var isGridLayout = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupToggleButton()
        setupRetryButton()
        observeUiState()
        setupSwipeRefresh()
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter { movie ->
            val intent = Intent(requireContext(), MovieDetailActivity::class.java).apply {
                putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.id)
                putExtra(MovieDetailActivity.EXTRA_MOVIE_TITLE, movie.title)
            }
            startActivity(intent)
        }

        binding.recyclerViewMovies.apply {
            adapter = movieAdapter
            setHasFixedSize(true)
        }

        // Set initial layout
        updateLayoutManager()
    }

    private fun setupToggleButton() {
        updateToggleButtonIcon()

        binding.buttonToggleView.setOnClickListener {
            isGridLayout = !isGridLayout
            updateLayoutManager()
            updateToggleButtonIcon()
            movieAdapter.setLayoutType(isGridLayout)
        }
    }

    private fun updateLayoutManager() {
        binding.recyclerViewMovies.layoutManager = if (isGridLayout) {
            GridLayoutManager(context, 2)
        } else {
            LinearLayoutManager(context)
        }
    }

    private fun updateToggleButtonIcon() {
        val iconRes = if (isGridLayout) {
            R.drawable.ic_list_view
        } else {
            R.drawable.ic_grid_view
        }
        binding.buttonToggleView.setImageResource(iconRes)

        val contentDescription = if (isGridLayout) {
            getString(R.string.toggle_view_grid_to_list)
        } else {
            getString(R.string.toggle_view_list_to_grid)
        }
        binding.buttonToggleView.contentDescription = contentDescription
    }

    private fun setupRetryButton() {
        binding.buttonRetry.setOnClickListener {
            viewModel.clearError()
            viewModel.loadBenedictCumberbatchMovies()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                // Update loading state
                binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                binding.swipeRefreshLayout.isRefreshing = state.isLoading

                // Show/hide error view
                if (state.error != null && state.movies.isEmpty()) {
                    // Show error view only if there are no movies to display
                    binding.errorView.visibility = View.VISIBLE
                    binding.textViewError.text = state.error
                    binding.swipeRefreshLayout.visibility = View.GONE
                } else {
                    // Hide error view and show movie list
                    binding.errorView.visibility = View.GONE
                    binding.swipeRefreshLayout.visibility = View.VISIBLE

                    // Update movie list
                    movieAdapter.submitList(state.movies)
                }
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadBenedictCumberbatchMovies()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
