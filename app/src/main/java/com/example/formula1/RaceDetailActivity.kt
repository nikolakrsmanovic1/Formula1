package com.example.formula1

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.formula1.data.ServiceLocator
import com.example.formula1.model.SessionType
import com.example.formula1.viewmodel.RaceDetailInfo
import com.example.formula1.viewmodel.RaceDetailViewModel
import com.example.formula1.viewmodel.UiState
import com.example.formula1.viewmodel.ViewModelFactory

class RaceDetailActivity : AppCompatActivity() {

    // Runda stize iz ekrana Raspored preko Intent-a
    private val round by lazy { intent.getIntExtra(EXTRA_RACE_NUMBER, 1) }

    private val viewModel: RaceDetailViewModel by viewModels {
        ViewModelFactory { RaceDetailViewModel(ServiceLocator.repository, round) }
    }

    private val sessionRows = intArrayOf(
        R.id.sessionRow1, R.id.sessionRow2, R.id.sessionRow3, R.id.sessionRow4, R.id.sessionRow5
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_race_detail)
        applyInsets()

        // Strelica nazad -> vraća na Raspored
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<View>(R.id.btnResults).setOnClickListener {
            Toast.makeText(this, R.string.toast_soon, Toast.LENGTH_SHORT).show()
        }

        // Ako ucitavanje ne uspe, dodir na tekst ispod naslova pokusava ponovo
        findViewById<View>(R.id.tvDetailLocation).setOnClickListener { viewModel.load() }

        viewModel.state.observe(this) { render(it) }
    }

    private fun render(state: UiState<RaceDetailInfo>) {
        val title = findViewById<TextView>(R.id.tvDetailTitle)
        val location = findViewById<TextView>(R.id.tvDetailLocation)
        when (state) {
            is UiState.Loading -> {
                title.setText(R.string.loading)
                location.text = ""
            }
            is UiState.Success -> {
                val race = state.data
                title.text = race.name
                location.text = getString(R.string.detail_location_format, race.locality, race.country, race.round)
            }
            is UiState.Empty -> {
                title.setText(R.string.race_not_found)
                location.text = ""
            }
            is UiState.Error -> {
                title.setText(R.string.loading)
                location.setText(errorMessageRes(state.kind))
            }
        }
        renderSessions((state as? UiState.Success)?.data)
    }

    /** Prikazuje onoliko redova koliko trka ima sesija (5), a ostale sakriva. */
    private fun renderSessions(race: RaceDetailInfo?) {
        val sessions = race?.sessions.orEmpty()
        sessionRows.forEachIndexed { index, rowId ->
            val row = findViewById<View>(rowId)
            val session = sessions.getOrNull(index)
            row.setVisible(session != null)
            if (session == null) return@forEachIndexed

            val nameView = row.findViewById<TextView>(R.id.tvSessionName)
            nameView.setText(sessionNameRes(session.type))
            val color = if (session.type == SessionType.RACE) R.color.accent_light else R.color.ink
            nameView.setTextColor(ContextCompat.getColor(this, color))
            row.findViewById<TextView>(R.id.tvSessionTime).text = session.time
        }
    }

    private fun sessionNameRes(type: SessionType): Int = when (type) {
        SessionType.FIRST_PRACTICE -> R.string.session_fp1
        SessionType.SECOND_PRACTICE -> R.string.session_fp2
        SessionType.THIRD_PRACTICE -> R.string.session_fp3
        SessionType.SPRINT_QUALIFYING -> R.string.session_sprint_qualifying
        SessionType.SPRINT -> R.string.session_sprint
        SessionType.QUALIFYING -> R.string.session_qualifying
        SessionType.RACE -> R.string.session_race
    }

    companion object {
        const val EXTRA_RACE_NUMBER = "race_number"
    }
}
