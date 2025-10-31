package com.rushdroid.benedictmovies.core.util

import java.util.Locale

/**
 * Utility class for currency-related operations
 */
object CurrencyUtil {

    /**
     * Formats a currency amount to a readable string with appropriate suffixes
     * @param amount The amount to format
     * @return Formatted string (e.g., "1.5B", "250.3M", "15.2K")
     */
    fun formatCurrency(amount: Long): String {
        return when {
            amount >= 1_000_000_000 -> String.format(Locale.getDefault(), "%.1fB", amount / 1_000_000_000.0)
            amount >= 1_000_000 -> String.format(Locale.getDefault(), "%.1fM", amount / 1_000_000.0)
            amount >= 1_000 -> String.format(Locale.getDefault(), "%.1fK", amount / 1_000.0)
            else -> amount.toString()
        }
    }
}
