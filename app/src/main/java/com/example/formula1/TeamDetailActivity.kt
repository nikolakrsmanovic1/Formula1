package com.example.formula1

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TeamDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_detail)
        applyInsets()

        // Pozicija tima stiže preko Intent-a iz Poretka (tab Timovi)
        val position = intent.getIntExtra(EXTRA_TEAM_POSITION, 1)
        findViewById<TextView>(R.id.tvTeamName).text =
            getString(R.string.team_name_format, position)
        findViewById<TextView>(R.id.tvTeamPosition).text =
            getString(R.string.team_position_format, position)

        // Strelica nazad -> vraća na Poredak
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        // Kartice statistike (privremeni podaci, kasnije iz API-ja)
        val stats = listOf(
            R.id.stat1 to R.string.stat_constructor_titles,
            R.id.stat2 to R.string.stat_wins,
            R.id.stat3 to R.string.stat_poles,
            R.id.stat4 to R.string.stat_podiums,
            R.id.stat5 to R.string.stat_fastest_laps,
            R.id.stat6 to R.string.stat_points
        )
        for ((id, labelRes) in stats) {
            val card = findViewById<View>(id)
            card.findViewById<TextView>(R.id.tvStatValue).setText(R.string.stat_value_placeholder)
            card.findViewById<TextView>(R.id.tvStatLabel).setText(labelRes)
        }
    }

    /** Vozaci tima se osvezavaju pri povratku (zvezdica omiljenog vozaca). */
    override fun onResume() {
        super.onResume()
        val position = intent.getIntExtra(EXTRA_TEAM_POSITION, 1)
        // Svaki tim ima dva vozaca: tim 1 -> vozaci 1 i 2, tim 2 -> vozaci 3 i 4...
        bindDriverRow(R.id.teamDriver1, position * 2 - 1, R.string.driver_name_format, R.string.team_placeholder)
        bindDriverRow(R.id.teamDriver2, position * 2, R.string.driver_name_format, R.string.team_placeholder)
    }

    companion object {
        const val EXTRA_TEAM_POSITION = "team_position"
    }
}
