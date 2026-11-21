package com.example.formula1.viewmodel

import com.example.formula1.model.SessionType
import com.example.formula1.model.TeamStanding

/** Red u listi trka. */
data class RaceItem(
    val round: Int,
    val name: String,
    val place: String,
    val date: String
)

/** Red u poretku: vozac (isDriver = true) ili tim (isDriver = false). */
data class StandingItem(
    val id: String,
    val position: Int,
    val title: String,
    val subtitle: String,
    val points: String,
    val isDriver: Boolean
)

/** Sledeca trka za karticu na Pocetnoj. */
data class NextRaceInfo(
    val name: String,
    val locality: String,
    val country: String,
    val startMillis: Long,
    val dateText: String
)

/** Jedna sesija trkackog vikenda sa vec formatiranim vremenom. */
data class SessionItem(val type: SessionType, val time: String)

/** Detalj jedne trke. */
data class RaceDetailInfo(
    val round: Int,
    val name: String,
    val locality: String,
    val country: String,
    val sessions: List<SessionItem>
)

/** Tim sa svojim vozacima (ekran Tim). */
data class TeamProfile(
    val team: TeamStanding,
    val drivers: List<StandingItem>
)
