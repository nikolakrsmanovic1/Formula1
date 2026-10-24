package com.example.formula1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        applyInsets()
        setupBottomNav(NavTab.SCHEDULE)

        findViewById<View>(R.id.btnFilter).setOnClickListener {
            Toast.makeText(this, R.string.toast_soon, Toast.LENGTH_SHORT).show()
        }

        val rows = intArrayOf(
            R.id.raceRow1, R.id.raceRow2, R.id.raceRow3,
            R.id.raceRow4, R.id.raceRow5, R.id.raceRow6
        )
        rows.forEachIndexed { index, rowId ->
            val raceNumber = index + 1
            val row = findViewById<View>(rowId)
            row.findViewById<TextView>(R.id.tvRaceRound).text =
                getString(R.string.race_round_format, raceNumber)
            row.findViewById<TextView>(R.id.tvRaceName).setText(R.string.race_name_placeholder)
            row.findViewById<TextView>(R.id.tvRacePlace).setText(R.string.race_place_placeholder)
            row.findViewById<TextView>(R.id.tvRaceDate).setText(R.string.race_date_placeholder)

            // Tap na trku -> Detalj trke (šalje broj trke preko Intent-a)
            row.setOnClickListener {
                val intent = Intent(this, RaceDetailActivity::class.java)
                intent.putExtra(RaceDetailActivity.EXTRA_RACE_NUMBER, raceNumber)
                startActivity(intent)
            }
        }
    }
}
