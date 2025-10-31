package com.rushdroid.benedictmovies.core.util

import com.rushdroid.benedictmovies.core.constants.Constants

object ImageUtil {

    fun getFullImageUrl(imagePath: String?, size: String = Constants.IMAGE_SIZE_W500): String {
        return if (imagePath.isNullOrBlank()) {
            ""
        } else {
            "${Constants.IMAGE_BASE_URL}$size$imagePath"
        }
    }

    fun getPosterUrl(posterPath: String?): String {
        return getFullImageUrl(posterPath, Constants.IMAGE_SIZE_W500)
    }

    fun getBackdropUrl(backdropPath: String?): String {
        return getFullImageUrl(backdropPath, Constants.IMAGE_SIZE_ORIGINAL)
    }
}
