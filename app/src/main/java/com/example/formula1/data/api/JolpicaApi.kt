package com.example.formula1.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit opis Jolpica F1 API-ja (https://api.jolpi.ca/ergast/).
 * Svaka funkcija je jedan HTTP GET; Retrofit sam pravi kod za poziv i pretvara JSON u klase iz Dto.kt.
 */
interface JolpicaApi {

    @GET("f1/current/races.json")
    suspend fun getRaces(@Query("limit") limit: Int): RacesResponse

    @GET("f1/current/driverstandings.json")
    suspend fun getDriverStandings(@Query("limit") limit: Int): DriverStandingsResponse

    @GET("f1/current/constructorstandings.json")
    suspend fun getConstructorStandings(@Query("limit") limit: Int): ConstructorStandingsResponse

    /** Bilo koji upit cija nas zanima samo ukupna brojka (MRData.total), npr. drivers/alonso/results/1.json. */
    @GET("f1/{path}")
    suspend fun getTotal(
        @Path(value = "path", encoded = true) path: String,
        @Query("limit") limit: Int
    ): TotalResponse

    /** Poredak vozaca po sezonama, npr. drivers/alonso/driverstandings/1.json (sezone u kojima je bio prvi). */
    @GET("f1/{path}")
    suspend fun getDriverStandingsAt(
        @Path(value = "path", encoded = true) path: String,
        @Query("limit") limit: Int
    ): DriverStandingsResponse

    @GET("f1/{path}")
    suspend fun getConstructorStandingsAt(
        @Path(value = "path", encoded = true) path: String,
        @Query("limit") limit: Int
    ): ConstructorStandingsResponse

    /** Koliko puta se vozac ili tim zavrsio trku na koji nacin, npr. drivers/alonso/status.json. */
    @GET("f1/{path}")
    suspend fun getStatuses(
        @Path(value = "path", encoded = true) path: String,
        @Query("limit") limit: Int
    ): StatusResponse
}
