package com.example.formula1

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.formula1.data.ServiceLocator
import com.example.formula1.model.DriverStanding
import com.example.formula1.model.DriverStats
import com.example.formula1.viewmodel.DriverDetailViewModel
import com.example.formula1.viewmodel.UiState
import com.example.formula1.viewmodel.ViewModelFactory

class DriverDetailActivity : AppCompatActivity() {

    // Id vozača stiže preko Intent-a (npr. "max_verstappen") i ide u ViewModel
    private val driverId: String by lazy { intent.getStringExtra(EXTRA_DRIVER_ID).orEmpty() }

    private val viewModel: DriverDetailViewModel by viewModels {
        ViewModelFactory { DriverDetailViewModel(ServiceLocator.repository, driverId) }
    }

    private var current: DriverStanding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_detail)
        applyInsets()

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        val btnFavorite = findViewById<ImageButton>(R.id.btnFavorite)
        btnFavorite.setOnClickListener { toggleFavorite(btnFavorite) }
        showFavorite(btnFavorite)

        val status = findViewById<TextView>(R.id.tvStatsStatus)
        status.setOnClickListener { viewModel.load() }

        // Nazivi kartica su stalni, vrednosti dolaze iz ViewModel-a
        val labels = listOf(
            R.string.stat_wins, R.string.stat_poles, R.string.stat_podiums, R.string.stat_dnf,
            R.string.stat_fastest_laps, R.string.stat_titles, R.string.stat_races, R.string.stat_points
        )
        for ((index, labelRes) in labels.withIndex()) {
            val card = statCard(index)
            card.findViewById<TextView>(R.id.tvStatLabel).setText(labelRes)
            card.findViewById<TextView>(R.id.tvStatValue).setText(R.string.stat_value_placeholder)
        }

        viewModel.profile.observe(this) { state ->
            when (state) {
                is UiState.Success -> {
                    current = state.data
                    showProfile(state.data)
                    showFavorite(btnFavorite)
                }
                is UiState.Empty -> findViewById<TextView>(R.id.tvDriverName)
                    .setText(R.string.driver_not_found)
                is UiState.Error -> findViewById<TextView>(R.id.tvDriverName)
                    .setText(errorMessageRes(state.kind))
                is UiState.Loading -> findViewById<TextView>(R.id.tvDriverName)
                    .setText(R.string.loading)
            }
        }

        viewModel.stats.observe(this) { state ->
            status.setVisible(state !is UiState.Success)
            when (state) {
                is UiState.Loading -> status.setText(R.string.stats_loading)
                is UiState.Error -> status.setText(errorMessageRes(state.kind))
                is UiState.Empty -> status.setText(R.string.driver_not_found)
                is UiState.Success -> showStats(state.data)
            }
        }
    }

    private fun statCard(index: Int): View = findViewById(
        listOf(R.id.stat1, R.id.stat2, R.id.stat3, R.id.stat4,
            R.id.stat5, R.id.stat6, R.id.stat7, R.id.stat8)[index]
    )

    private fun setStat(index: Int, value: String) {
        statCard(index).findViewById<TextView>(R.id.tvStatValue).text = value
    }

    private fun showProfile(driver: DriverStanding) {
        findViewById<TextView>(R.id.tvDriverName).text = driver.fullName
        findViewById<TextView>(R.id.tvDriverTeam).text = driver.teamName
        findViewById<TextView>(R.id.tvDriverNumber).text =
            driver.number?.let { getString(R.string.driver_number_format, it) }
                ?: getString(R.string.stat_value_placeholder)
        setStat(7, driver.points)
    }

    private fun showStats(stats: DriverStats) {
        setStat(0, stats.wins.toString())
        setStat(1, stats.poles.toString())
        setStat(2, stats.podiums.toString())
        setStat(3, stats.dnf.toString())
        setStat(4, stats.fastestLaps.toString())
        setStat(5, stats.titles.toString())
        setStat(6, stats.races.toString())
    }

    private fun toggleFavorite(button: ImageButton) {
        val driver = current ?: return
        if (Favorites.getId(this) == driver.driverId) {
            Favorites.clear(this)
            Toast.makeText(this, R.string.toast_favorite_removed, Toast.LENGTH_SHORT).show()
        } else {
            Favorites.set(this, driver.driverId, driver.fullName)
            Toast.makeText(this, R.string.toast_favorite_added, Toast.LENGTH_SHORT).show()
        }
        showFavorite(button)
    }

    private fun showFavorite(button: ImageButton) {
        val isFavorite = Favorites.getId(this) == driverId
        button.setImageResource(if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star)
    }

    companion object {
        const val EXTRA_DRIVER_ID = "driver_id"
    }
}
