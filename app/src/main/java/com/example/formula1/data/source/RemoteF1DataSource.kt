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
            val titles = async { driverTitles("$base/driverstandings/1.json") }
            val dnf = async { dnfCount("$base/status.json") }
            val winCount = wins.await()
            DriverStats(
                wins = winCount,
                poles = poles.await(),
                podiums = winCount + second.await() + third.await(),
                dnf = dnf.await(),
                fastestLaps = fastest.await(),
                titles = titles.await(),
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
            val titles = async { teamTitles("$base/constructorstandings/1.json") }
            val winCount = wins.await()
            TeamStats(
                titles = titles.await(),
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
        throw DataException(ErrorKind.SERVER, e)
    } catch (e: IOException) {
        throw DataException(ErrorKind.NETWORK, e)
    } catch (e: Exception) {
        throw DataException(ErrorKind.UNKNOWN, e)
    }

    private suspend fun total(path: String): Int = limiter.withPermit {
        delay(PAUSE_MILLIS)
        api.getTotal(path, 1).mrData?.total?.toIntOrNull() ?: 0
    }

    /** Broj sezona u kojima je vozac zavrsio na prvom mestu. */
    private suspend fun driverTitles(path: String): Int = limiter.withPermit {
        delay(PAUSE_MILLIS)
        api.getDriverStandingsAt(path, LIMIT_ALL).mrData?.table?.lists.orEmpty().size
    }

    private suspend fun teamTitles(path: String): Int = limiter.withPermit {
        delay(PAUSE_MILLIS)
        api.getConstructorStandingsAt(path, LIMIT_ALL).mrData?.table?.lists.orEmpty().size
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
        const val PAUSE_MILLIS = 250L
    }
}
