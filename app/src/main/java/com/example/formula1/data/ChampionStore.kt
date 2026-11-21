package com.example.formula1.data

import android.content.Context

/**
 * Pamti ko je bio sampion koje sezone. Zavrsene sezone se nikad ne menjaju, pa se svaki
 * podatak trazi sa interneta samo jednom, a posle ostaje sacuvan i kad se aplikacija ugasi.
 */
interface ChampionStore {
    fun get(key: String): String?
    fun put(key: String, value: String)
}

/** Bez trajnog pamcenja (koristi se kad nema Context-a, npr. u testu). */
class NoChampionStore : ChampionStore {
    override fun get(key: String): String? = null
    override fun put(key: String, value: String) {}
}

/** Trajno pamcenje u SharedPreferences. */
class PrefsChampionStore(context: Context) : ChampionStore {
    private val prefs = context.applicationContext.getSharedPreferences("champions", Context.MODE_PRIVATE)
    override fun get(key: String): String? = prefs.getString(key, null)
    override fun put(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
}
