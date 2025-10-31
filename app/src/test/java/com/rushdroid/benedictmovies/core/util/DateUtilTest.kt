package com.rushdroid.benedictmovies.core.util

import android.content.Context
import com.rushdroid.benedictmovies.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

/**
 * Unit tests for DateUtil
 */
class DateUtilTest {

    private val mockContext = mock(Context::class.java).apply {
        `when`(getString(R.string.unknown_year)).thenReturn("Unknown")
    }

    @Test
    fun `extractYear should extract year from valid date string`() {
        assertEquals("2023", DateUtil.extractYear("2023-05-15", mockContext))
        assertEquals("2020", DateUtil.extractYear("2020-12-31", mockContext))
        assertEquals("1995", DateUtil.extractYear("1995-01-01", mockContext))
    }

    @Test
    fun `extractYear should handle date strings with only year`() {
        assertEquals("2023", DateUtil.extractYear("2023", mockContext))
        assertEquals("1999", DateUtil.extractYear("1999", mockContext))
    }

    @Test
    fun `extractYear should return unknown for empty or blank strings`() {
        assertEquals("Unknown", DateUtil.extractYear("", mockContext))
        assertEquals("Unknown", DateUtil.extractYear("   ", mockContext))
    }

    @Test
    fun `extractYear should return unknown for strings shorter than 4 characters`() {
        assertEquals("Unknown", DateUtil.extractYear("20", mockContext))
        assertEquals("Unknown", DateUtil.extractYear("202", mockContext))
    }

    @Test
    fun `extractYear should handle malformed date strings`() {
        assertEquals("2023", DateUtil.extractYear("2023-invalid-date", mockContext))
        assertEquals("1995", DateUtil.extractYear("1995-99-99", mockContext))
    }
}
