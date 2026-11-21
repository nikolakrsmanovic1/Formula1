package com.example.formula1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * ViewModel pravi sistem, pa mu sam ne moze dati parametre (Repository, id...).
 * Ova fabrika ih ubacuje preko lambde: ViewModelFactory { HomeViewModel(repository) }.
 */
class ViewModelFactory<T : ViewModel>(private val builder: () -> T) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <V : ViewModel> create(modelClass: Class<V>): V = builder() as V
}
