package com.example.formula1

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.formula1.databinding.ActivityStandingsBinding

class StandingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStandingsBinding
    private lateinit var adapter: StandingsAdapter

    // Koji tab je trenutno prikazan (vozaci ili timovi)
    private var showingDrivers = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStandingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyInsets(binding.root)
        setupBottomNav(binding.bottomNav, NavTab.STANDINGS)

        // Klik na stavku: vozac -> ekran Vozac, tim -> ekran Tim
        adapter = StandingsAdapter { item -> openDetail(item) }
        binding.rvStandings.layoutManager = LinearLayoutManager(this)
        binding.rvStandings.addItemDecoration(
            ItemSpacing(resources.getDimensionPixelSize(R.dimen.spacing_sm))
        )
        binding.rvStandings.adapter = adapter

        binding.tabDrivers.setOnClickListener { showTab(drivers = true) }
        binding.tabTeams.setOnClickListener { showTab(drivers = false) }
        showTab(drivers = true)
    }

    /** Kad se vratimo sa ekrana Vozac, osvezi zvezdicu omiljenog vozaca. */
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    /** Prebacuje izmedju poretka vozaca i poretka timova (zamenjuje listu u adapteru). */
    private fun showTab(drivers: Boolean) {
        showingDrivers = drivers
        styleTab(binding.tabDrivers, active = drivers)
        styleTab(binding.tabTeams, active = !drivers)

        // Privremeni podaci (kasnije iz API-ja)
        val count = resources.getInteger(if (drivers) R.integer.driver_count else R.integer.team_count)
        val nameRes = if (drivers) R.string.driver_name_format else R.string.team_name_format
        val subtitleRes = if (drivers) R.string.team_placeholder else R.string.team_subtitle
        val items = List(count) { index ->
            StandingItem(
                position = index + 1,
                title = getString(nameRes, index + 1),
                subtitle = getString(subtitleRes),
                points = getString(R.string.points_placeholder)
            )
        }
        adapter.setItems(items, driverList = drivers)

        // Prazna lista -> poruka umesto praznog ekrana
        showEmptyMessage(binding.rvStandings, binding.tvEmpty, items.size)
    }

    private fun openDetail(item: StandingItem) {
        val intent = if (showingDrivers) {
            Intent(this, DriverDetailActivity::class.java)
                .putExtra(DriverDetailActivity.EXTRA_DRIVER_POSITION, item.position)
        } else {
            Intent(this, TeamDetailActivity::class.java)
                .putExtra(TeamDetailActivity.EXTRA_TEAM_POSITION, item.position)
        }
        startActivity(intent)
    }

    private fun styleTab(tab: TextView, active: Boolean) {
        tab.setBackgroundResource(if (active) R.drawable.bg_tab_active else R.drawable.bg_card)
        tab.setTextColor(ContextCompat.getColor(this, if (active) R.color.white else R.color.ink))
    }
}
