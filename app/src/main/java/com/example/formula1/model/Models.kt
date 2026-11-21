package com.example.formula1.model

/** Vrste sesija trkackog vikenda. */
enum class SessionType {
    FIRST_PRACTICE, SECOND_PRACTICE, THIRD_PRACTICE,
    SPRINT_QUALIFYING, SPRINT, QUALIFYING, RACE
}

/** Jedna sesija (trening, kvalifikacije, trka...) sa pocetkom u UTC milisekundama. */
data class RaceSession(val type: SessionType, val startMillis: Long)

/** Jedna trka u kalendaru sezone. */
data class Race(
    val round: Int,
    val name: String,
    val circuitName: String,
    val locality: String,
    val country: String,
    val startMillis: Long,
    val sessions: List<RaceSession>
)

/** Vozac u poretku sampionata. */
data class DriverStanding(
    val position: Int,
    val driverId: String,
    val firstName: String,
    val lastName: String,
    val number: Int?,
    val teamId: String,
    val teamName: String,
    val points: String,
    val wins: Int
) {
    val fullName: String get() = "$firstName $lastName".trim()
}

/** Tim (konstruktor) u poretku sampionata. */
data class TeamStanding(
    val position: Int,
    val teamId: String,
    val name: String,
    val nationality: String,
    val points: String,
    val wins: Int
)

/** Statistika karijere jednog vozaca. */
data class DriverStats(
    val wins: Int,
    val poles: Int,
    val podiums: Int,
    val dnf: Int,
    val fastestLaps: Int,
    val titles: Int?,
    val races: Int
)

/** Statistika jednog tima kroz istoriju. */
data class TeamStats(
    val titles: Int?,
    val wins: Int,
    val poles: Int,
    val podiums: Int,
    val fastestLaps: Int
)

/** Vrsta greske pri ucitavanju podataka (ekran na osnovu nje bira poruku). */
enum class ErrorKind { NETWORK, SERVER, UNKNOWN }

/** Jedina greska koju sloj podataka baca prema ostatku aplikacije. */
class DataException(val kind: ErrorKind, cause: Throwable? = null) : Exception(cause)
