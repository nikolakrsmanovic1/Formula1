package com.example.formula1.viewmodel

import com.example.formula1.model.DriverStanding
import com.example.formula1.model.Race
import com.example.formula1.model.TeamStanding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Pretvaranje modela u ono sto ekran prikazuje (formatiranje datuma i redova liste).

internal fun formatDate(millis: Long): String =
    SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault()).format(Date(millis))

internal fun formatDateTime(millis: Long): String =
    SimpleDateFormat("dd.MM.yyyy. HH:mm", Locale.getDefault()).format(Date(millis))

internal fun formatSessionTime(millis: Long): String =
    SimpleDateFormat("EEE dd.MM. HH:mm", Locale.getDefault()).format(Date(millis))

internal fun Race.toRaceItem() = RaceItem(
    round = round,
    name = name,
    place = listOf(locality, country).filter { it.isNotBlank() }.joinToString(", "),
    date = formatDate(startMillis)
)

internal fun DriverStanding.toStandingItem() = StandingItem(
    id = driverId,
    position = position,
    title = fullName,
    subtitle = teamName,
    points = points,
    isDriver = true
)

internal fun TeamStanding.toStandingItem() = StandingItem(
    id = teamId,
    position = position,
    title = name,
    subtitle = nationality,
    points = points,
    isDriver = false
)
