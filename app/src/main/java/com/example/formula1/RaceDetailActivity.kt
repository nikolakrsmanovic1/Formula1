package com.example.formula1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.formula1.databinding.ActivityRaceDetailBinding

class RaceDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRaceDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRaceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyInsets(binding.root)

        // Broj trke stiže iz ekrana Raspored preko Intent-a
        val raceNumber = intent.getIntExtra(EXTRA_RACE_NUMBER, 1)
        binding.tvDetailTitle.text = getString(R.string.race_name_format, raceNumber)

        // Strelica nazad -> vraća na Raspored
        binding.btnBack.setOnClickListener { finish() }

        val sessionNames = resources.getStringArray(R.array.session_names)
        val rows = listOf(
            binding.sessionRow1, binding.sessionRow2, binding.sessionRow3,
            binding.sessionRow4, binding.sessionRow5
        )
        rows.forEachIndexed { index, row ->
            row.tvSessionName.text = sessionNames[index]
            if (index == rows.lastIndex) {
                row.tvSessionName.setTextColor(ContextCompat.getColor(this, R.color.accent_light))
            }
            row.tvSessionTime.setText(R.string.session_time_placeholder)
        }

        binding.btnResults.setOnClickListener {
            Toast.makeText(this, R.string.toast_soon, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_RACE_NUMBER = "race_number"
    }
}
