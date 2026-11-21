package com.example.formula1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.formula1.data.F1Repository
import com.example.formula1.model.DataException
import kotlinx.coroutines.launch

/** Stanje ekrana Raspored: lista trka sezone. */
class ScheduleViewModel(private val repository: F1Repository) : ViewModel() {

    private val _state = MutableLiveData<UiState<List<RaceItem>>>(UiState.Loading)
    val state: LiveData<UiState<List<RaceItem>>> get() = _state

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                val races = repository.getRaces()
                if (races.isEmpty()) UiState.Empty else UiState.Success(races.map { it.toRaceItem() })
            } catch (e: DataException) {
                UiState.Error(e.kind)
            }
        }
    }
}
