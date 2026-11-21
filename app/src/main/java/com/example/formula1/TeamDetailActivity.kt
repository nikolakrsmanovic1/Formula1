package com.example.formula1

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.formula1.data.ServiceLocator
import com.example.formula1.model.TeamStats
import com.example.formula1.viewmodel.TeamDetailViewModel
import com.example.formula1.viewmodel.TeamProfile
import com.example.formula1.viewmodel.UiState
import com.example.formula1.viewmodel.ViewModelFactory

class TeamDetailActivity : AppCompatActivity() {

    private val teamId: String by lazy { intent.getStringExtra(EXTRA_TEAM_ID).orEmpty() }

    private val viewModel: TeamDetailViewModel by viewModels {
        ViewModelFactory { TeamDetailViewModel(ServiceLocator.repository, teamId) }
    }

    private val statIds = listOf(
        R.id.stat1, R.id.stat2, R.id.stat3, R.id.stat4, R.id.stat5, R.id.stat6
    )

    private var profile: TeamProfile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_detail)
        applyInsets()

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        val status = findViewById<TextView>(R.id.tvStatsStatus)
        status.setOnClickListener { viewModel.load() }

        val labels = listOf(
            R.string.stat_constructor_titles, R.string.stat_wins, R.string.stat_poles,
            R.string.stat_podiums, R.string.stat_fastest_laps, R.string.stat_points
        )
        for ((id, labelRes) in statIds.zip(labels)) {
            val card = findViewById<View>(id)
            card.findViewById<TextView>(R.id.tvStatLabel).setText(labelRes)
            card.findViewById<TextView>(R.id.tvStatValue).setText(R.string.stat_value_placeholder)
        }

        viewModel.profile.observe(this) { state ->
            when (state) {
                is UiState.Success -> {
                    profile = state.data
                    showProfile(state.data)
                }
                is UiState.Empty -> findViewById<TextView>(R.id.tvTeamName)
                    .setText(R.string.team_not_found)
                is UiState.Error -> findViewById<TextView>(R.id.tvTeamName)
                    .setText(errorMessageRes(state.kind))
                is UiState.Loading -> findViewById<TextView>(R.id.tvTeamName)
                    .setText(R.string.loading)
            }
        }

        viewModel.stats.observe(this) { state ->
            status.setVisible(state !is UiState.Success)
            when (state) {
                is UiState.Loading -> status.setText(R.string.stats_loading)
                is UiState.Error -> status.setText(errorMessageRes(state.kind))
                is UiState.Empty -> status.setText(R.string.team_not_found)
                is UiState.Success -> showStats(state.data)
            }
        }
    }

    /** Zvezdica omiljenog vozaca se osvezava pri povratku sa ekrana vozaca. */
    override fun onResume() {
        super.onResume()
        profile?.let { showDrivers(it) }
    }

    private fun showProfile(data: TeamProfile) {
        findViewById<TextView>(R.id.tvTeamName).text = data.team.name
        findViewById<TextView>(R.id.tvTeamSubtitle).text = data.team.nationality
        findViewById<TextView>(R.id.tvTeamPosition).text =
            getString(R.string.team_position_format, data.team.position)
        findViewById<View>(R.id.stat6).findViewById<TextView>(R.id.tvStatValue).text =
            data.team.points
        showDrivers(data)
    }

    private fun showDrivers(data: TeamProfile) {
        val rows = listOf(R.id.teamDriver1, R.id.teamDriver2)
        for ((index, rowId) in rows.withIndex()) {
            val driver = data.drivers.getOrNull(index)
            findViewById<View>(rowId).setVisible(driver != null)
            if (driver != null) bindDriverRow(rowId, driver)
        }
    }

    private fun showStats(stats: TeamStats) {
        val values = listOf(stats.titles, stats.wins, stats.poles, stats.podiums, stats.fastestLaps)
        for ((id, value) in statIds.zip(values)) {
            findViewById<View>(id).findViewById<TextView>(R.id.tvStatValue).text = value.toString()
        }
    }

    companion object {
        const val EXTRA_TEAM_ID = "team_id"
    }
}
