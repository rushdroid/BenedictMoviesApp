package com.rushdroid.benedictmovies.core.util

import com.rushdroid.benedictmovies.R

/**
 * Helper function to execute API calls with centralized error handling.
 * Automatically handles exceptions and provides user-friendly error messages.
 * Supports validation for empty lists and null responses.
 *
 * @param T The type of data returned by the API call
 * @param stringResourceProvider Provider for accessing string resources
 * @param validateNotEmpty If true, validates that list responses are not empty
 * @param validateNotNull If true, validates that responses are not null
 * @param emptyMessage Custom message to show when validation fails (optional)
 * @param apiCall The suspend function that makes the API call
 * @return Result<T> containing either success data or failure with error message
 */
suspend inline fun <reified T> safeApiCall(
    stringResourceProvider: StringResourceProvider,
    validateNotEmpty: Boolean = false,
    validateNotNull: Boolean = false,
    emptyMessage: String? = null,
    crossinline apiCall: suspend () -> T
): Result<T> {
    return try {
        val response = apiCall()

        // Validate empty list if requested
        if (validateNotEmpty && response is List<*> && response.isEmpty()) {
            val errorMessage = emptyMessage
                ?: stringResourceProvider.getString(R.string.error_no_movies_found)
            return Result.failure(Exception(errorMessage))
        }

        // Validate not null if requested
        if (validateNotNull && response == null) {
            val errorMessage = emptyMessage
                ?: stringResourceProvider.getString(R.string.error_data_not_found)
            return Result.failure(Exception(errorMessage))
        }

        Result.success(response)
    } catch (e: Exception) {
        Result.failure(Exception(ErrorHandler.getErrorMessage(stringResourceProvider, e), e))
    }
}
