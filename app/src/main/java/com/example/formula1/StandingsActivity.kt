package com.example.formula1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class StandingsActivity : AppCompatActivity() {

    // Redovi u layoutu (22 vozaca); u tabu Timovi se prikazuje samo prvih 11
    private val rows = intArrayOf(
        R.id.driverRow1, R.id.driverRow2, R.id.driverRow3, R.id.driverRow4,
        R.id.driverRow5, R.id.driverRow6, R.id.driverRow7, R.id.driverRow8,
        R.id.driverRow9, R.id.driverRow10, R.id.driverRow11, R.id.driverRow12,
        R.id.driverRow13, R.id.driverRow14, R.id.driverRow15, R.id.driverRow16,
        R.id.driverRow17, R.id.driverRow18, R.id.driverRow19, R.id.driverRow20,
        R.id.driverRow21, R.id.driverRow22
    )

    // Koji tab je trenutno prikazan (potrebno da se lista osvezi pri povratku na ekran)
    private var showingDrivers = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_standings)
        applyInsets()
        setupBottomNav(NavTab.STANDINGS)

        findViewById<TextView>(R.id.tabDrivers).setOnClickListener { showTab(drivers = true) }
        findViewById<TextView>(R.id.tabTeams).setOnClickListener { showTab(drivers = false) }
        showTab(drivers = true)
    }

    /** Kad se vratimo sa ekrana Vozac, osvezi zvezdicu omiljenog vozaca. */
    override fun onResume() {
        super.onResume()
        showTab(drivers = showingDrivers)
    }

    /** Prebacuje između poretka vozača i poretka timova. */
    private fun showTab(drivers: Boolean) {
        showingDrivers = drivers
        styleTab(R.id.tabDrivers, active = drivers)
        styleTab(R.id.tabTeams, active = !drivers)

        val nameRes = if (drivers) R.string.driver_name_format else R.string.team_name_format
        val subtitleRes = if (drivers) R.string.team_placeholder else R.string.team_subtitle
        val count = resources.getInteger(if (drivers) R.integer.driver_count else R.integer.team_count)
        rows.forEachIndexed { index, rowId ->
            val row = findViewById<View>(rowId)
            if (index >= count) {
                row.visibility = View.GONE
                return@forEachIndexed
            }
            row.visibility = View.VISIBLE
            val position = index + 1
            bindDriverRow(rowId, position, nameRes, subtitleRes, opensDetail = drivers)
            // Klik na tim -> ekran Tim
            if (!drivers) {
                row.setOnClickListener {
                    val intent = Intent(this, TeamDetailActivity::class.java)
                    intent.putExtra(TeamDetailActivity.EXTRA_TEAM_POSITION, position)
                    startActivity(intent)
                }
            }
        }
    }

    private fun styleTab(tabId: Int, active: Boolean) {
        val tab = findViewById<TextView>(tabId)
        tab.setBackgroundResource(if (active) R.drawable.bg_tab_active else R.drawable.bg_card)
        tab.setTextColor(ContextCompat.getColor(this, if (active) R.color.white else R.color.muted))
    }
}
