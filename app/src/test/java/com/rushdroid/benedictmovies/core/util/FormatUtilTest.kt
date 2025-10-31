package com.rushdroid.benedictmovies.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for FormatUtil
 */
class FormatUtilTest {

    @Test
    fun `formatRating should format ratings with one decimal place`() {
        assertEquals("7.5", FormatUtil.formatRating(7.5))
        assertEquals("8.0", FormatUtil.formatRating(8.0))
        assertEquals("6.7", FormatUtil.formatRating(6.73))
    }

    @Test
    fun `formatRating should round to one decimal place`() {
        assertEquals("7.6", FormatUtil.formatRating(7.56))
        assertEquals("7.5", FormatUtil.formatRating(7.54))
        assertEquals("10.0", FormatUtil.formatRating(9.95))
    }

    @Test
    fun `formatRating should handle edge cases`() {
        assertEquals("0.0", FormatUtil.formatRating(0.0))
        assertEquals("10.0", FormatUtil.formatRating(10.0))
        assertEquals("0.1", FormatUtil.formatRating(0.05))
    }
}
