package com.example.formula1.data

import android.content.Context
import com.example.formula1.data.api.JolpicaClient
import com.example.formula1.data.source.RemoteF1DataSource

/** Mesto gde se sklapa sloj podataka: jedan Repository za celu aplikaciju. */
object ServiceLocator {
    private var store: ChampionStore = NoChampionStore()

    /** Poziva se jednom iz F1App. */
    fun init(context: Context) {
        store = PrefsChampionStore(context)
    }

    val repository: F1Repository by lazy {
        F1Repository(RemoteF1DataSource(JolpicaClient.api, store))
    }
}
