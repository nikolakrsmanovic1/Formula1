package com.example.formula1

import android.content.Context

/**
 * Cuva omiljenog vozaca u SharedPreferences.
 * Za sada se vozac identifikuje pozicijom u poretku (0 = niko nije izabran);
 * kad se poveze Jolpica API, ovde ide driverId.
 */
object Favorites {
    private const val PREFS_NAME = "favorites"
    private const val KEY_DRIVER = "driver_position"
    const val NONE = 0

    fun get(context: Context): Int =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(KEY_DRIVER, NONE)

    fun set(context: Context, position: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putInt(KEY_DRIVER, position).apply()
    }

    fun clear(context: Context) = set(context, NONE)
}
