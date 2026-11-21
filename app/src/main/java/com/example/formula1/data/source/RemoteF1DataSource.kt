package com.example.formula1.data.source

import com.example.formula1.data.api.ConstructorStandingDto
import com.example.formula1.data.api.DriverStandingDto
import com.example.formula1.data.api.JolpicaApi
import com.example.formula1.data.api.RaceDto
import com.example.formula1.data.api.SessionDto
import com.example.formula1.model.DataException
import com.example.formula1.model.DriverStanding
import com.example.formula1.model.DriverStats
import com.example.formula1.model.ErrorKind
import com.example.formula1.model.Race
import com.example.formula1.model.RaceSession
import com.example.formula1.model.SessionType
import com.example.formula1.model.TeamStanding
import com.example.formula1.model.TeamStats
import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import retrofit2.HttpException
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.cancellation.CancellationException

/** Podaci sa Jolpica API-ja: poziva Retrofit i pretvara odgovor u nase modele. */
class RemoteF1DataSource(private val api: JolpicaApi) : F1DataSource {

    // Statistika trazi vise poziva; ogranicavamo koliko ih ide istovremeno (API ima limit po sekundi).
    private val limiter = Semaphore(MAX_PARALLEL_CALLS)

    override suspend fun races(): List<Race> = call {
        api.getRaces(LIMIT_ALL).mrData?.raceTable?.races.orEmpty()
            .mapNotNull { it.toRace() }
            .sortedBy { it.round }
    }

    override suspend fun driverStandings(): List<DriverStanding> = call {
        api.getDriverStandings(LIMIT_ALL).mrData?.table?.lists.orEmpty()
            .firstOrNull()?.standings.orEmpty()
            .mapNotNull { it.toDriverStanding() }
            .sortedBy { it.position }
    }

    override suspend fun teamStandings(): List<TeamStanding> = call {
        api.getConstructorStandings(LIMIT_ALL).mrData?.table?.lists.orEmpty()
            .firstOrNull()?.standings.orEmpty()
            .mapNotNull { it.toTeamStanding() }
            .sortedBy { it.position }
    }

    override suspend fun driverStats(driverId: String): DriverStats = call {
        val base = "drivers/$driverId"
        coroutineScope {
            val wins = async { total("$base/results/1.json") }
            val second = async { total("$base/results/2.json") }
            val third = async { total("$base/results/3.json") }
            val poles = async { total("$base/qualifying/1.json") }
            val fastest = async { total("$base/fastest/1/results.json") }
            val races = async { total("$base/results.json") }
            val dnf = async { dnfCount("$base/status.json") }
            val winCount = wins.await()
            DriverStats(
                wins = winCount,
                poles = poles.await(),
                podiums = winCount + second.await() + third.await(),
                dnf = dnf.await(),
                fastestLaps = fastest.await(),
                races = races.await()
            )
        }
    }

    override suspend fun teamStats(teamId: String): TeamStats = call {
        val base = "constructors/$teamId"
        coroutineScope {
            val wins = async { total("$base/results/1.json") }
            val second = async { total("$base/results/2.json") }
            val third = async { total("$base/results/3.json") }
            val poles = async { total("$base/qualifying/1.json") }
            val fastest = async { total("$base/fastest/1/results.json") }
            val winCount = wins.await()
            TeamStats(
                wins = winCount,
                poles = poles.await(),
                podiums = winCount + second.await() + third.await(),
                fastestLaps = fastest.await()
            )
        }
    }

    // --- Pomocne funkcije ---

    /** Pretvara sve greske mreze u DataException, da ostatak aplikacije ne zna za Retrofit. */
    private suspend fun <T> call(block: suspend () -> T): T = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: DataException) {
        throw e
    } catch (e: HttpException) {
        Log.e(TAG, "HTTP ${e.code()} za ${e.response()?.raw()?.request?.url}", e)
        throw DataException(ErrorKind.SERVER, e)
    } catch (e: IOException) {
        Log.e(TAG, "Mrezna greska", e)
        throw DataException(ErrorKind.NETWORK, e)
    } catch (e: Exception) {
        Log.e(TAG, "Neocekivana greska", e)
        throw DataException(ErrorKind.UNKNOWN, e)
    }

    private suspend fun total(path: String): Int = limiter.withPermit {
        delay(PAUSE_MILLIS)
        api.getTotal(path, 1).mrData?.total?.toIntOrNull() ?: 0
    }

    /**
     * Titule: poredak sa filterom po vozacu ili poziciji API ne podrzava (HTTP 400), pa prvo
     * uzmemo sezone u kojima je vozac ucestvovao, a zatim za svaku zavrsenu sezonu pogledamo ko
     * je bio prvi. Sampioni sezona se pamte, pa ih sledeci vozac ili tim ne trazi ponovo.
     */
    override suspend fun driverTitles(driverId: String): Int = call {
        countTitles("drivers/$driverId/seasons.json", FIRST_DRIVER_SEASON) { season ->
            driverChampions.getOrPut(season) {
                val first = limiter.withPermit {
                    delay(PAUSE_MILLIS)
                    api.getDriverStandingsAt("$season/driverstandings.json", 1)
                }.mrData?.table?.lists.orEmpty().firstOrNull()?.standings.orEmpty().firstOrNull()
                first?.driver?.driverId.orEmpty()
            } == driverId
        }
    }

    override suspend fun teamTitles(teamId: String): Int = call {
        countTitles("constructors/$teamId/seasons.json", FIRST_TEAM_SEASON) { season ->
            teamChampions.getOrPut(season) {
                val first = limiter.withPermit {
                    delay(PAUSE_MILLIS)
                    api.getConstructorStandingsAt("$season/constructorstandings.json", 1)
                }.mrData?.table?.lists.orEmpty().firstOrNull()?.standings.orEmpty().firstOrNull()
                first?.constructor?.constructorId.orEmpty()
            } == teamId
        }
    }

    private val driverChampions = HashMap<String, String>()
    private val teamChampions = HashMap<String, String>()

    private suspend fun countTitles(
        seasonsPath: String,
        firstSeason: Int,
        isChampion: suspend (String) -> Boolean
    ): Int {
        val unfinished = unfinishedSeason()
        val seasons = limiter.withPermit {
            delay(PAUSE_MILLIS)
            api.getSeasons(seasonsPath, LIMIT_ALL)
        }.mrData?.table?.seasons.orEmpty()
            .mapNotNull { it.season }
            .filter { it != unfinished && (it.toIntOrNull() ?: 0) >= firstSeason }
        var titles = 0
        for (season in seasons) {
            if (isChampion(season)) titles++
        }
        return titles
    }

    /** Godina sezone koja jos traje (poslednja trka u kalendaru je u buducnosti), inace null. */
    private suspend fun unfinishedSeason(): String? {
        val last = races().lastOrNull() ?: return null
        if (last.startMillis < System.currentTimeMillis()) return null
        val format = SimpleDateFormat("yyyy", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.format(java.util.Date(last.startMillis))
    }

    /** DNF = trke koje nisu zavrsene regularno ("Finished" ili "+N Lap(s)"). */
    private suspend fun dnfCount(path: String): Int = limiter.withPermit {
        delay(PAUSE_MILLIS)
        api.getStatuses(path, LIMIT_ALL).mrData?.table?.status.orEmpty()
            .filter { isRetirement(it.status.orEmpty()) }
            .sumOf { it.count?.toIntOrNull() ?: 0 }
    }

    private fun isRetirement(status: String): Boolean {
        val text = status.trim()
        val finished = text == "Finished" || text.startsWith("+")
        val neverRaced = text.startsWith("Did not")
        return !finished && !neverRaced
    }

    // --- Pretvaranje DTO -> model ---

    private fun RaceDto.toRace(): Race? {
        val roundNumber = round?.toIntOrNull() ?: return null
        val start = parseDateTime(date, time) ?: return null
        val sessions = listOfNotNull(
            firstPractice.toSession(SessionType.FIRST_PRACTICE),
            secondPractice.toSession(SessionType.SECOND_PRACTICE),
            thirdPractice.toSession(SessionType.THIRD_PRACTICE),
            (sprintQualifying ?: sprintShootout).toSession(SessionType.SPRINT_QUALIFYING),
            sprint.toSession(SessionType.SPRINT),
            qualifying.toSession(SessionType.QUALIFYING),
            RaceSession(SessionType.RACE, start)
        ).sortedBy { it.startMillis }
        return Race(
            round = roundNumber,
            name = raceName.orEmpty(),
            circuitName = circuit?.circuitName.orEmpty(),
            locality = circuit?.location?.locality.orEmpty(),
            country = circuit?.location?.country.orEmpty(),
            startMillis = start,
            sessions = sessions
        )
    }

    private fun SessionDto?.toSession(type: SessionType): RaceSession? {
        val start = parseDateTime(this?.date, this?.time) ?: return null
        return RaceSession(type, start)
    }

    private fun DriverStandingDto.toDriverStanding(): DriverStanding? {
        val id = driver?.driverId ?: return null
        val team = constructors.orEmpty().lastOrNull()
        return DriverStanding(
            position = position?.toIntOrNull() ?: return null,
            driverId = id,
            firstName = driver.givenName.orEmpty(),
            lastName = driver.familyName.orEmpty(),
            number = driver.permanentNumber?.toIntOrNull(),
            teamId = team?.constructorId.orEmpty(),
            teamName = team?.name.orEmpty(),
            points = points.orEmpty(),
            wins = wins?.toIntOrNull() ?: 0
        )
    }

    private fun ConstructorStandingDto.toTeamStanding(): TeamStanding? {
        val id = constructor?.constructorId ?: return null
        return TeamStanding(
            position = position?.toIntOrNull() ?: return null,
            teamId = id,
            name = constructor.name.orEmpty(),
            nationality = constructor.nationality.orEmpty(),
            points = points.orEmpty(),
            wins = wins?.toIntOrNull() ?: 0
        )
    }

    /** "2026-11-22" + "04:00:00Z" -> milisekunde (UTC). Ako vreme nedostaje, uzima se ponoc. */
    private fun parseDateTime(date: String?, time: String?): Long? {
        if (date.isNullOrBlank()) return null
        val clock = time?.takeIf { it.isNotBlank() }
            ?.let { if (it.endsWith("Z")) it else it + "Z" }
            ?: "00:00:00Z"
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        format.timeZone = TimeZone.getTimeZone("UTC")
        return try {
            format.parse("${date}T$clock")?.time
        } catch (e: ParseException) {
            null
        }
    }

    private companion object {
        const val LIMIT_ALL = 100
        const val MAX_PARALLEL_CALLS = 1
        const val TAG = "F1"
        const val FIRST_DRIVER_SEASON = 1950
        const val FIRST_TEAM_SEASON = 1958
        const val PAUSE_MILLIS = 250L
    }
}
