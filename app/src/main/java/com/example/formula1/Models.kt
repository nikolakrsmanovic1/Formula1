package com.example.formula1

/** Jedna trka u rasporedu. */
data class Race(
    val number: Int,
    val name: String,
    val place: String,
    val date: String
)

/** Jedan red poretka - isti oblik za vozace i za timove. */
data class StandingItem(
    val position: Int,
    val title: String,
    val subtitle: String,
    val points: String
)
