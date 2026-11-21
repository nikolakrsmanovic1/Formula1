package com.example.formula1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.formula1.data.F1Repository
import com.example.formula1.model.DataException
import kotlinx.coroutines.launch

/** Stanje Pocetne: sledeca trka, top 3 vozaca i omiljeni vozac. */
class HomeViewModel(private val repository: F1Repository) : ViewModel() {

    private val _nextRace = MutableLiveData<UiState<NextRaceInfo>>(UiState.Loading)
    val nextRace: LiveData<UiState<NextRaceInfo>> get() = _nextRace

    private val _top3 = MutableLiveData<UiState<List<StandingItem>>>(UiState.Loading)
    val top3: LiveData<UiState<List<StandingItem>>> get() = _top3

    private val _favorite = MutableLiveData<StandingItem?>(null)
    val favorite: LiveData<StandingItem?> get() = _favorite

    private var allDrivers: List<StandingItem> = emptyList()
    private var favoriteId: String? = null

    init {
        load()
    }

    fun load() {
        loadNextRace()
        loadTopDrivers()
    }

    /** Ekran javlja koji je vozac omiljen (cuva se u SharedPreferences, a ne ovde). */
    fun setFavoriteId(id: String?) {
        favoriteId = id
        publishFavorite()
    }

    private fun loadNextRace() {
        viewModelScope.launch {
            _nextRace.value = UiState.Loading
            _nextRace.value = try {
                val now = System.currentTimeMillis()
                val next = repository.getRaces().firstOrNull { it.startMillis > now }
                if (next == null) {
                    UiState.Empty
                } else {
                    UiState.Success(
                        NextRaceInfo(
                            name = next.name,
                            locality = next.locality,
                            country = next.country,
                            startMillis = next.startMillis,
                            dateText = formatDateTime(next.startMillis)
                        )
                    )
                }
            } catch (e: DataException) {
                UiState.Error(e.kind)
            }
        }
    }

    private fun loadTopDrivers() {
        viewModelScope.launch {
            _top3.value = UiState.Loading
            _top3.value = try {
                allDrivers = repository.getDriverStandings().map { it.toStandingItem() }
                publishFavorite()
                if (allDrivers.isEmpty()) UiState.Empty else UiState.Success(allDrivers.take(TOP_COUNT))
            } catch (e: DataException) {
                UiState.Error(e.kind)
            }
        }
    }

    private fun publishFavorite() {
        _favorite.value = allDrivers.find { it.id == favoriteId }
    }

    private companion object {
        const val TOP_COUNT = 3
    }
}
