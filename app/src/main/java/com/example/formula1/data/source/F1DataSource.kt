package com.example.formula1.data.source

import com.example.formula1.model.DriverStanding
import com.example.formula1.model.DriverStats
import com.example.formula1.model.Race
import com.example.formula1.model.TeamStanding
import com.example.formula1.model.TeamStats

/**
 * Ugovor: "ume da vrati F1 podatke", bez obzira odakle. Sada su to podaci sa interneta
 * (RemoteF1DataSource), a u testu se moze ubaciti lazni izvor.
 * Sve funkcije bacaju samo DataException.
 */
interface F1DataSource {
    suspend fun races(): List<Race>
    suspend fun driverStandings(): List<DriverStanding>
    suspend fun teamStandings(): List<TeamStanding>
    suspend fun driverStats(driverId: String): DriverStats
    suspend fun teamStats(teamId: String): TeamStats

    /** Broj osvojenih titula (trazi dosta poziva, pa se ucitava odvojeno od ostale statistike). */
    suspend fun driverTitles(driverId: String): Int
    suspend fun teamTitles(teamId: String): Int
}
