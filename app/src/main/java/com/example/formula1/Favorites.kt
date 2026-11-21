package com.example.formula1

import android.content.Context

/**
 * Cuva omiljenog vozaca u SharedPreferences: njegov id sa API-ja (npr. "max_verstappen")
 * i ime, da se ime moze prikazati i bez interneta.
 */
object Favorites {
    private const val PREFS_NAME = "favorites"
    private const val KEY_ID = "driver_id"
    private const val KEY_NAME = "driver_name"

    fun getId(context: Context): String? = prefs(context).getString(KEY_ID, null)

    fun getName(context: Context): String? = prefs(context).getString(KEY_NAME, null)

    fun set(context: Context, id: String, name: String) {
        prefs(context).edit().putString(KEY_ID, id).putString(KEY_NAME, name).apply()
    }

    fun clear(context: Context) {
        prefs(context).edit().remove(KEY_ID).remove(KEY_NAME).apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
