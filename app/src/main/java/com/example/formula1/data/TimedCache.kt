package com.example.formula1.data

/** Pamti jednu vrednost odredjeno vreme, da ne trazimo iste podatke sa interneta pri svakom ekranu. */
class TimedCache<T : Any>(private val ttlMillis: Long) {
    private var value: T? = null
    private var savedAt = 0L

    fun get(): T? {
        val saved = value ?: return null
        return if (System.currentTimeMillis() - savedAt < ttlMillis) saved else null
    }

    fun put(newValue: T) {
        value = newValue
        savedAt = System.currentTimeMillis()
    }
}
