package com.example.formula1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        applyInsets()
        setupBottomNav(NavTab.HOME)

        // Ikonica korisnika -> Profil
        findViewById<View>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Odbrojavanje (privremeni podaci, kasnije iz API-ja)
        bindCountdown(R.id.cdDays, R.string.cd_days_value, R.string.cd_days_label)
        bindCountdown(R.id.cdHours, R.string.cd_hours_value, R.string.cd_hours_label)
        bindCountdown(R.id.cdMinutes, R.string.cd_minutes_value, R.string.cd_minutes_label)

        // Top 3 vozača
        intArrayOf(R.id.driverRow1, R.id.driverRow2, R.id.driverRow3)
            .forEachIndexed { index, rowId ->
                bindDriverRow(rowId, index + 1, R.string.driver_name_format, R.string.team_placeholder)
            }
    }

    private fun bindCountdown(boxId: Int, valueRes: Int, labelRes: Int) {
        val box = findViewById<View>(boxId)
        box.findViewById<TextView>(R.id.tvCdValue).setText(valueRes)
        box.findViewById<TextView>(R.id.tvCdLabel).setText(labelRes)
    }
}
