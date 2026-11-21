package com.example.formula1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.formula1.data.F1Repository
import com.example.formula1.model.DataException
import kotlinx.coroutines.launch

/**
 * Stanje ekrana Poredak. Ucitava i vozace i timove, a ekranu objavljuje samo onu listu
 * koja je izabrana u tabu.
 */
class StandingsViewModel(private val repository: F1Repository) : ViewModel() {

    private var drivers: UiState<List<StandingItem>> = UiState.Loading
    private var teams: UiState<List<StandingItem>> = UiState.Loading

    private val _showDrivers = MutableLiveData(true)
    val showDrivers: LiveData<Boolean> get() = _showDrivers

    private val _state = MutableLiveData<UiState<List<StandingItem>>>(UiState.Loading)
    val state: LiveData<UiState<List<StandingItem>>> get() = _state

    init {
        load()
    }

    fun load() {
        drivers = UiState.Loading
        teams = UiState.Loading
        publish()
        viewModelScope.launch {
            drivers = try {
                val list = repository.getDriverStandings().map { it.toStandingItem() }
                if (list.isEmpty()) UiState.Empty else UiState.Success(list)
            } catch (e: DataException) {
                UiState.Error(e.kind)
            }
            publish()
        }
        viewModelScope.launch {
            teams = try {
                val list = repository.getTeamStandings().map { it.toStandingItem() }
                if (list.isEmpty()) UiState.Empty else UiState.Success(list)
            } catch (e: DataException) {
                UiState.Error(e.kind)
            }
            publish()
        }
    }

    fun selectTab(drivers: Boolean) {
        _showDrivers.value = drivers
        publish()
    }

    private fun publish() {
        _state.value = if (_showDrivers.value == true) drivers else teams
    }
}
