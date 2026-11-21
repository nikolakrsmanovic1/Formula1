package com.example.formula1.data

import com.example.formula1.data.api.JolpicaClient
import com.example.formula1.data.source.RemoteF1DataSource

/** Mesto gde se sklapa sloj podataka: jedan Repository za celu aplikaciju. */
object ServiceLocator {
    val repository: F1Repository by lazy {
        F1Repository(RemoteF1DataSource(JolpicaClient.api))
    }
}
