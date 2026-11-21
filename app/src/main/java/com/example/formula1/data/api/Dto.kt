package com.example.formula1.data.api

import com.google.gson.annotations.SerializedName

// Oblik JSON odgovora Jolpica (Ergast) API-ja. Svi brojevi stizu kao tekst ("24"),
// a sva polja su nullable jer Gson ne proverava da li su stigla.

// --- Raspored ---
data class RacesResponse(@SerializedName("MRData") val mrData: RacesMrData?)
data class RacesMrData(@SerializedName("RaceTable") val raceTable: RaceTableDto?)
data class RaceTableDto(@SerializedName("Races") val races: List<RaceDto>?)

data class RaceDto(
    val round: String?,
    val raceName: String?,
    @SerializedName("Circuit") val circuit: CircuitDto?,
    val date: String?,
    val time: String?,
    @SerializedName("FirstPractice") val firstPractice: SessionDto?,
    @SerializedName("SecondPractice") val secondPractice: SessionDto?,
    @SerializedName("ThirdPractice") val thirdPractice: SessionDto?,
    @SerializedName("SprintQualifying") val sprintQualifying: SessionDto?,
    @SerializedName("SprintShootout") val sprintShootout: SessionDto?,
    @SerializedName("Sprint") val sprint: SessionDto?,
    @SerializedName("Qualifying") val qualifying: SessionDto?
)

data class SessionDto(val date: String?, val time: String?)
data class CircuitDto(
    val circuitName: String?,
    @SerializedName("Location") val location: LocationDto?
)
data class LocationDto(val locality: String?, val country: String?)

// --- Poredak vozaca ---
data class DriverStandingsResponse(@SerializedName("MRData") val mrData: DriverStandingsMrData?)
data class DriverStandingsMrData(@SerializedName("StandingsTable") val table: DriverStandingsTable?)
data class DriverStandingsTable(@SerializedName("StandingsLists") val lists: List<DriverStandingsList>?)
data class DriverStandingsList(
    val season: String?,
    @SerializedName("DriverStandings") val standings: List<DriverStandingDto>?)

data class DriverStandingDto(
    val position: String?,
    val points: String?,
    val wins: String?,
    @SerializedName("Driver") val driver: DriverDto?,
    @SerializedName("Constructors") val constructors: List<ConstructorDto>?
)

data class DriverDto(
    val driverId: String?,
    val permanentNumber: String?,
    val code: String?,
    val givenName: String?,
    val familyName: String?
)

// --- Poredak timova ---
data class ConstructorStandingsResponse(@SerializedName("MRData") val mrData: ConstructorStandingsMrData?)
data class ConstructorStandingsMrData(@SerializedName("StandingsTable") val table: ConstructorStandingsTable?)
data class ConstructorStandingsTable(@SerializedName("StandingsLists") val lists: List<ConstructorStandingsList>?)
data class ConstructorStandingsList(
    val season: String?,
    @SerializedName("ConstructorStandings") val standings: List<ConstructorStandingDto>?)

data class ConstructorStandingDto(
    val position: String?,
    val points: String?,
    val wins: String?,
    @SerializedName("Constructor") val constructor: ConstructorDto?
)

data class ConstructorDto(
    val constructorId: String?,
    val name: String?,
    val nationality: String?
)

// --- Brojaci (MRData.total) ---
data class TotalResponse(@SerializedName("MRData") val mrData: TotalMrData?)
data class TotalMrData(val total: String?)

// --- Statusi zavrsetka trke (za DNF) ---
data class StatusResponse(@SerializedName("MRData") val mrData: StatusMrData?)
data class StatusMrData(@SerializedName("StatusTable") val table: StatusTableDto?)
data class StatusTableDto(@SerializedName("Status") val status: List<StatusDto>?)
data class StatusDto(val statusId: String?, val count: String?, val status: String?)

// --- Sezone u kojima je vozac ili tim ucestvovao ---
data class SeasonsResponse(@SerializedName("MRData") val mrData: SeasonsMrData?)
data class SeasonsMrData(@SerializedName("SeasonTable") val table: SeasonTableDto?)
data class SeasonTableDto(@SerializedName("Seasons") val seasons: List<SeasonDto>?)
data class SeasonDto(val season: String?)
