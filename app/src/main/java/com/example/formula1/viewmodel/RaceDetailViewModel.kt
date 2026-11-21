package com.example.formula1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.formula1.data.F1Repository
import com.example.formula1.model.DataException
import kotlinx.coroutines.launch

/** Stanje ekrana Detalj trke (runda stize iz ekrana Raspored). */
class RaceDetailViewModel(
    private val repository: F1Repository,
    private val round: Int
) : ViewModel() {

    private val _state = MutableLiveData<UiState<RaceDetailInfo>>(UiState.Loading)
    val state: LiveData<UiState<RaceDetailInfo>> get() = _state

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                val race = repository.getRace(round)
                if (race == null) {
                    UiState.Empty
                } else {
                    UiState.Success(
                        RaceDetailInfo(
                            round = race.round,
                            name = race.name,
                            locality = race.locality,
                            country = race.country,
                            sessions = race.sessions.map { SessionItem(it.type, formatSessionTime(it.startMillis)) }
                        )
                    )
                }
            } catch (e: DataException) {
                UiState.Error(e.kind)
            }
        }
    }
}
