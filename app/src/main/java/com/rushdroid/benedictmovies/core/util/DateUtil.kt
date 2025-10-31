package com.rushdroid.benedictmovies.core.util

import android.content.Context
import com.rushdroid.benedictmovies.R

/**
 * Utility class for date-related operations
 */
object DateUtil {

    /**
     * Extracts the year from a date string (YYYY-MM-DD format)
     * @param dateString The date string to extract year from
     * @param context Context for accessing string resources
     * @return The year as a string, or "Unknown" if invalid
     */
    fun extractYear(dateString: String, context: Context): String {
        return if (dateString.isNotBlank() && dateString.length >= 4) {
            dateString.substring(0, 4)
        } else {
            context.getString(R.string.unknown_year)
        }
    }
}
