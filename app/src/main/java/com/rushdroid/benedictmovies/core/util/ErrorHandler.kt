package com.rushdroid.benedictmovies.core.util

import com.rushdroid.benedictmovies.R
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Utility class for handling and converting exceptions to user-friendly error messages.
 * Uses string resources for internationalization support.
 */
object ErrorHandler {

    /**
     * Converts exceptions to localized user-friendly error messages.
     *
     * @param stringProvider Provider for accessing string resources
     * @param exception The exception to convert
     * @return Localized error message string
     */
    fun getErrorMessage(stringProvider: StringResourceProvider, exception: Throwable): String {
        return when (exception) {
            is UnknownHostException -> stringProvider.getString(R.string.error_no_internet_connection)
            is SocketTimeoutException -> stringProvider.getString(R.string.error_network_timeout)
            is IOException -> stringProvider.getString(R.string.error_network_general)
            is HttpException -> {
                when (exception.code()) {
                    401 -> stringProvider.getString(R.string.error_invalid_api_key)
                    404 -> stringProvider.getString(R.string.error_resource_not_found)
                    429 -> stringProvider.getString(R.string.error_too_many_requests)
                    in 500..599 -> stringProvider.getString(R.string.error_server_error)
                    else -> stringProvider.getString(R.string.error_http_general, exception.code())
                }
            }
            else -> stringProvider.getString(R.string.error_unknown)
        }
    }

    /**
     * Convenience method that accepts Context directly for backward compatibility.
     *
     * @param context Android context for accessing string resources
     * @param exception The exception to convert
     * @return Localized error message string
     */
    fun getErrorMessage(context: android.content.Context, exception: Throwable): String {
        return getErrorMessage(AndroidStringResourceProvider(context), exception)
    }
}
