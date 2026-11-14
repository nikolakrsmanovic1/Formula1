package com.example.formula1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.formula1.databinding.ActivityTeamDetailBinding

class TeamDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeamDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyInsets(binding.root)

        // Pozicija tima stiže preko Intent-a iz Poretka (tab Timovi)
        val position = intent.getIntExtra(EXTRA_TEAM_POSITION, 1)
        binding.tvTeamName.text = getString(R.string.team_name_format, position)
        binding.tvTeamPosition.text = getString(R.string.team_position_format, position)

        // Strelica nazad -> vraća na Poredak
        binding.btnBack.setOnClickListener { finish() }

        // Kartice statistike (privremeni podaci, kasnije iz API-ja)
        val stats = listOf(
            binding.stat1 to R.string.stat_constructor_titles,
            binding.stat2 to R.string.stat_wins,
            binding.stat3 to R.string.stat_poles,
            binding.stat4 to R.string.stat_podiums,
            binding.stat5 to R.string.stat_fastest_laps,
            binding.stat6 to R.string.stat_points
        )
        for ((card, labelRes) in stats) {
            card.tvStatValue.setText(R.string.stat_value_placeholder)
            card.tvStatLabel.setText(labelRes)
        }
    }

    /** Vozaci tima se osvezavaju pri povratku (zvezdica omiljenog vozaca). */
    override fun onResume() {
        super.onResume()
        val position = intent.getIntExtra(EXTRA_TEAM_POSITION, 1)
        // Svaki tim ima dva vozaca: tim 1 -> vozaci 1 i 2, tim 2 -> vozaci 3 i 4...
        bindDriverRow(binding.teamDriver1, position * 2 - 1, R.string.driver_name_format, R.string.team_placeholder)
        bindDriverRow(binding.teamDriver2, position * 2, R.string.driver_name_format, R.string.team_placeholder)
    }

    companion object {
        const val EXTRA_TEAM_POSITION = "team_position"
    }
}
