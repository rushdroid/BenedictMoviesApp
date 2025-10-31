package com.rushdroid.benedictmovies.core.util

import java.util.Locale

/**
 * Utility class for formatting operations
 */
object FormatUtil {

    /**
     * Formats a movie rating to display with one decimal place
     * @param rating The rating value to format
     * @return Formatted rating string (e.g., "7.5")
     */
    fun formatRating(rating: Double): String {
        return String.format(Locale.getDefault(), "%.1f", rating)
    }
}
