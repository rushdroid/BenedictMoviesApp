package com.rushdroid.benedictmovies.core.util

/**
 * Interface for providing string resources.
 * This abstraction allows repositories to access localized strings without depending on Android Context.
 */
interface StringResourceProvider {
    fun getString(resId: Int): String
    fun getString(resId: Int, vararg formatArgs: Any): String
}
