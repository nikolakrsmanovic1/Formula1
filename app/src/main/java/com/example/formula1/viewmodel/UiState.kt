package com.example.formula1.viewmodel

import com.example.formula1.model.ErrorKind

/**
 * Stanje jednog ekrana ili njegovog dela. Ekran ne pita "da li je greska?", nego samo
 * prikazuje ono sto stanje kaze (Loading, Success, Empty ili Error).
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    object Empty : UiState<Nothing>()
    data class Error(val kind: ErrorKind) : UiState<Nothing>()
}
