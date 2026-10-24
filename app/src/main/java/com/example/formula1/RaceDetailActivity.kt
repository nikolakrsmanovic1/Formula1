package com.example.formula1

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RaceDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_race_detail)
        applyInsets()

        // Broj trke stiže iz ekrana Raspored preko Intent-a
        val raceNumber = intent.getIntExtra(EXTRA_RACE_NUMBER, 1)
        findViewById<TextView>(R.id.tvDetailTitle).text =
            getString(R.string.race_name_format, raceNumber)

        // Strelica nazad -> vraća na Raspored
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        val sessionNames = resources.getStringArray(R.array.session_names)
        val rows = intArrayOf(
            R.id.sessionRow1, R.id.sessionRow2, R.id.sessionRow3,
            R.id.sessionRow4, R.id.sessionRow5
        )
        rows.forEachIndexed { index, rowId ->
            val row = findViewById<View>(rowId)
            row.findViewById<TextView>(R.id.tvSessionName).text = sessionNames[index]
            row.findViewById<TextView>(R.id.tvSessionTime).setText(R.string.session_time_placeholder)
        }

        findViewById<View>(R.id.btnResults).setOnClickListener {
            Toast.makeText(this, R.string.toast_soon, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_RACE_NUMBER = "race_number"
    }
}
