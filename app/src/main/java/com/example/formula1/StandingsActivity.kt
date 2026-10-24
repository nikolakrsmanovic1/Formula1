package com.example.formula1

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class StandingsActivity : AppCompatActivity() {

    private val rows = intArrayOf(
        R.id.driverRow1, R.id.driverRow2, R.id.driverRow3, R.id.driverRow4,
        R.id.driverRow5, R.id.driverRow6, R.id.driverRow7, R.id.driverRow8
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_standings)
        applyInsets()
        setupBottomNav(NavTab.STANDINGS)

        findViewById<TextView>(R.id.tabDrivers).setOnClickListener { showTab(drivers = true) }
        findViewById<TextView>(R.id.tabTeams).setOnClickListener { showTab(drivers = false) }
        showTab(drivers = true)
    }

    /** Prebacuje između poretka vozača i poretka timova. */
    private fun showTab(drivers: Boolean) {
        styleTab(R.id.tabDrivers, active = drivers)
        styleTab(R.id.tabTeams, active = !drivers)

        val nameRes = if (drivers) R.string.driver_name_format else R.string.team_name_format
        val subtitleRes = if (drivers) R.string.team_placeholder else R.string.team_subtitle
        rows.forEachIndexed { index, rowId ->
            bindDriverRow(rowId, index + 1, nameRes, subtitleRes)
        }
    }

    private fun styleTab(tabId: Int, active: Boolean) {
        val tab = findViewById<TextView>(tabId)
        tab.setBackgroundResource(if (active) R.drawable.bg_tab_active else R.drawable.bg_card)
        tab.setTextColor(ContextCompat.getColor(this, if (active) R.color.white else R.color.muted))
    }
}
