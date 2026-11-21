package com.example.formula1.data

import com.example.formula1.data.source.F1DataSource
import com.example.formula1.model.DriverStanding
import com.example.formula1.model.DriverStats
import com.example.formula1.model.Race
import com.example.formula1.model.TeamStanding
import com.example.formula1.model.TeamStats

/**
 * Jedina vrata do F1 podataka za ostatak aplikacije. Ne zna da li podaci dolaze sa interneta
 * (to zna samo DataSource) i pamti ih kratko vreme da se ne ucitavaju iznova.
 */
class F1Repository(private val source: F1DataSource) {

    private val racesCache = TimedCache<List<Race>>(LIST_TTL_MILLIS)
    private val driversCache = TimedCache<List<DriverStanding>>(LIST_TTL_MILLIS)
    private val teamsCache = TimedCache<List<TeamStanding>>(LIST_TTL_MILLIS)
    private val driverStatsCache = HashMap<String, TimedCache<DriverStats>>()
    private val teamStatsCache = HashMap<String, TimedCache<TeamStats>>()

    suspend fun getRaces(): List<Race> =
        racesCache.get() ?: source.races().also { racesCache.put(it) }

    suspend fun getRace(round: Int): Race? = getRaces().find { it.round == round }

    suspend fun getDriverStandings(): List<DriverStanding> =
        driversCache.get() ?: source.driverStandings().also { driversCache.put(it) }

    suspend fun getTeamStandings(): List<TeamStanding> =
        teamsCache.get() ?: source.teamStandings().also { teamsCache.put(it) }

    suspend fun getDriver(driverId: String): DriverStanding? =
        getDriverStandings().find { it.driverId == driverId }

    suspend fun getTeam(teamId: String): TeamStanding? =
        getTeamStandings().find { it.teamId == teamId }

    /** Vozaci jednog tima, po poretku u sampionatu. */
    suspend fun getDriversOfTeam(teamId: String): List<DriverStanding> =
        getDriverStandings().filter { it.teamId == teamId }

    suspend fun getDriverStats(driverId: String): DriverStats {
        val cache = driverStatsCache.getOrPut(driverId) { TimedCache(STATS_TTL_MILLIS) }
        return cache.get() ?: source.driverStats(driverId).also { cache.put(it) }
    }

    suspend fun getTeamStats(teamId: String): TeamStats {
        val cache = teamStatsCache.getOrPut(teamId) { TimedCache(STATS_TTL_MILLIS) }
        return cache.get() ?: source.teamStats(teamId).also { cache.put(it) }
    }

    private companion object {
        const val LIST_TTL_MILLIS = 10 * 60 * 1000L
        const val STATS_TTL_MILLIS = 60 * 60 * 1000L
    }
}
