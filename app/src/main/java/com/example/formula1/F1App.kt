package com.example.formula1

import android.app.Application
import com.example.formula1.data.ServiceLocator

/** Pokrece se pre svih ekrana; ovde sklapamo sloj podataka (treba mu Context za trajni keš). */
class F1App : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
