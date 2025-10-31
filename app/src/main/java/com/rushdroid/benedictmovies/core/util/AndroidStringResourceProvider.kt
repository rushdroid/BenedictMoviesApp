package com.rushdroid.benedictmovies.core.util

import android.content.Context

/**
 * Android implementation of StringResourceProvider that uses Context to access string resources.
 */
class AndroidStringResourceProvider(
    private val context: Context
) : StringResourceProvider {

    override fun getString(resId: Int): String {
        return context.getString(resId)
    }

    override fun getString(resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}
