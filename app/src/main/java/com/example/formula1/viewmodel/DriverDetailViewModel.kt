package com.example.formula1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.formula1.data.F1Repository
import com.example.formula1.model.DataException
import com.example.formula1.model.DriverStanding
import com.example.formula1.model.DriverStats
import kotlinx.coroutines.launch

/**
 * Stanje ekrana Vozac. Osnovni podaci (ime, tim) dolaze iz poretka i stignu odmah,
 * a statistika karijere trazi vise poziva pa se ucitava posebno.
 */
class DriverDetailViewModel(
    private val repository: F1Repository,
    private val driverId: String
) : ViewModel() {

    private val _profile = MutableLiveData<UiState<DriverStanding>>(UiState.Loading)
    val profile: LiveData<UiState<DriverStanding>> get() = _profile

    private val _stats = MutableLiveData<UiState<DriverStats>>(UiState.Loading)
    val stats: LiveData<UiState<DriverStats>> get() = _stats

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _profile.value = UiState.Loading
            _profile.value = try {
                val driver = repository.getDriver(driverId)
                if (driver == null) UiState.Empty else UiState.Success(driver)
            } catch (e: DataException) {
                UiState.Error(e.kind)
            }
        }
        viewModelScope.launch {
            _stats.value = UiState.Loading
            _stats.value = try {
                UiState.Success(repository.getDriverStats(driverId))
            } catch (e: DataException) {
                UiState.Error(e.kind)
            }
        }
    }
}
