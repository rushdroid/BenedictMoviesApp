package com.rushdroid.benedictmovies.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for CurrencyUtil
 */
class CurrencyUtilTest {

    @Test
    fun `formatCurrency should format billions correctly`() {
        assertEquals("1.5B", CurrencyUtil.formatCurrency(1_500_000_000))
        assertEquals("2.0B", CurrencyUtil.formatCurrency(2_000_000_000))
        assertEquals("10.3B", CurrencyUtil.formatCurrency(10_300_000_000))
    }

    @Test
    fun `formatCurrency should format millions correctly`() {
        assertEquals("1.5M", CurrencyUtil.formatCurrency(1_500_000))
        assertEquals("250.3M", CurrencyUtil.formatCurrency(250_300_000))
        assertEquals("999.9M", CurrencyUtil.formatCurrency(999_900_000))
    }

    @Test
    fun `formatCurrency should format thousands correctly`() {
        assertEquals("1.5K", CurrencyUtil.formatCurrency(1_500))
        assertEquals("15.2K", CurrencyUtil.formatCurrency(15_200))
        assertEquals("999.9K", CurrencyUtil.formatCurrency(999_900))
    }

    @Test
    fun `formatCurrency should return plain number for amounts under 1000`() {
        assertEquals("0", CurrencyUtil.formatCurrency(0))
        assertEquals("1", CurrencyUtil.formatCurrency(1))
        assertEquals("999", CurrencyUtil.formatCurrency(999))
    }

    @Test
    fun `formatCurrency should handle edge cases`() {
        assertEquals("1.0B", CurrencyUtil.formatCurrency(1_000_000_000))
        assertEquals("1.0M", CurrencyUtil.formatCurrency(1_000_000))
        assertEquals("1.0K", CurrencyUtil.formatCurrency(1_000))
    }
}
