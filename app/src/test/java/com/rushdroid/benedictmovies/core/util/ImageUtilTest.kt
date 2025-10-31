package com.rushdroid.benedictmovies.core.util

import com.rushdroid.benedictmovies.core.constants.Constants
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for ImageUtil
 */
class ImageUtilTest {

    @Test
    fun `getFullImageUrl should return complete URL with valid path and size`() {
        val imagePath = "/example_image.jpg"
        val size = "w500"
        val expectedUrl = "${Constants.IMAGE_BASE_URL}${size}${imagePath}"

        val result = ImageUtil.getFullImageUrl(imagePath, size)

        assertEquals(expectedUrl, result)
    }

    @Test
    fun `getFullImageUrl should return empty string for null path`() {
        val result = ImageUtil.getFullImageUrl(null, "w500")

        assertEquals("", result)
    }

    @Test
    fun `getFullImageUrl should return empty string for blank path`() {
        val result = ImageUtil.getFullImageUrl("", "w500")

        assertEquals("", result)
    }

    @Test
    fun `getFullImageUrl should return empty string for whitespace-only path`() {
        val result = ImageUtil.getFullImageUrl("   ", "w500")

        assertEquals("", result)
    }

    @Test
    fun `getFullImageUrl should use default size when not specified`() {
        val imagePath = "/test_image.jpg"
        val expectedUrl = "${Constants.IMAGE_BASE_URL}${Constants.IMAGE_SIZE_W500}${imagePath}"

        val result = ImageUtil.getFullImageUrl(imagePath)

        assertEquals(expectedUrl, result)
    }

    @Test
    fun `getPosterUrl should return poster URL with w500 size`() {
        val posterPath = "/poster_image.jpg"
        val expectedUrl = "${Constants.IMAGE_BASE_URL}${Constants.IMAGE_SIZE_W500}${posterPath}"

        val result = ImageUtil.getPosterUrl(posterPath)

        assertEquals(expectedUrl, result)
    }

    @Test
    fun `getPosterUrl should return empty string for null poster path`() {
        val result = ImageUtil.getPosterUrl(null)

        assertEquals("", result)
    }

    @Test
    fun `getBackdropUrl should return backdrop URL with original size`() {
        val backdropPath = "/backdrop_image.jpg"
        val expectedUrl = "${Constants.IMAGE_BASE_URL}${Constants.IMAGE_SIZE_ORIGINAL}${backdropPath}"

        val result = ImageUtil.getBackdropUrl(backdropPath)

        assertEquals(expectedUrl, result)
    }

    @Test
    fun `getBackdropUrl should return empty string for null backdrop path`() {
        val result = ImageUtil.getBackdropUrl(null)

        assertEquals("", result)
    }

    @Test
    fun `getFullImageUrl should handle different image sizes`() {
        val imagePath = "/test.jpg"

        val w500Result = ImageUtil.getFullImageUrl(imagePath, "w500")
        val originalResult = ImageUtil.getFullImageUrl(imagePath, "original")
        val w200Result = ImageUtil.getFullImageUrl(imagePath, "w200")

        assertEquals("${Constants.IMAGE_BASE_URL}w500${imagePath}", w500Result)
        assertEquals("${Constants.IMAGE_BASE_URL}original${imagePath}", originalResult)
        assertEquals("${Constants.IMAGE_BASE_URL}w200${imagePath}", w200Result)
    }

    @Test
    fun `getFullImageUrl should handle paths with special characters`() {
        val imagePath = "/test-image_with%20spaces.jpg"
        val expectedUrl = "${Constants.IMAGE_BASE_URL}w500${imagePath}"

        val result = ImageUtil.getFullImageUrl(imagePath, "w500")

        assertEquals(expectedUrl, result)
    }
}
