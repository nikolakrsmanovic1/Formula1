package com.example.formula1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.formula1.data.F1Repository
import com.example.formula1.model.DataException
import com.example.formula1.model.TeamStats
import kotlinx.coroutines.launch

/** Stanje ekrana Tim: osnovni podaci sa vozacima tima i statistika tima. */
class TeamDetailViewModel(
    private val repository: F1Repository,
    private val teamId: String
) : ViewModel() {

    private val _profile = MutableLiveData<UiState<TeamProfile>>(UiState.Loading)
    val profile: LiveData<UiState<TeamProfile>> get() = _profile

    private val _stats = MutableLiveData<UiState<TeamStats>>(UiState.Loading)
    val stats: LiveData<UiState<TeamStats>> get() = _stats

    private val _titles = MutableLiveData<UiState<Int>>(UiState.Loading)
    val titles: LiveData<UiState<Int>> get() = _titles

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _profile.value = UiState.Loading
            _profile.value = try {
                val team = repository.getTeam(teamId)
                if (team == null) {
                    UiState.Empty
                } else {
                    val drivers = repository.getDriversOfTeam(teamId).map { it.toStandingItem() }
                    UiState.Success(TeamProfile(team, drivers))
                }
            } catch (e: DataException) {
                UiState.Error(e.kind)
            }
        }
        viewModelScope.launch {
            _stats.value = UiState.Loading
            _stats.value = try {
                UiState.Success(repository.getTeamStats(teamId))
            } catch (e: DataException) {
                UiState.Error(e.kind)
            }
        }
        viewModelScope.launch {
            _titles.value = UiState.Loading
            _titles.value = try {
                UiState.Success(repository.getTeamTitles(teamId))
            } catch (e: DataException) {
                UiState.Error(e.kind)
            }
        }
    }
}
