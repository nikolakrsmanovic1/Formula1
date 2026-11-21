package com.example.formula1

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.formula1.data.ServiceLocator
import com.example.formula1.viewmodel.StandingsViewModel
import com.example.formula1.viewmodel.ViewModelFactory

class StandingsActivity : AppCompatActivity() {

    private val viewModel: StandingsViewModel by viewModels {
        ViewModelFactory { StandingsViewModel(ServiceLocator.repository) }
    }

    private lateinit var adapter: StandingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_standings)
        applyInsets()
        setupBottomNav(NavTab.STANDINGS)

        // Klik na stavku: vozac -> ekran Vozac, tim -> ekran Tim
        adapter = StandingsAdapter { item ->
            if (item.isDriver) openDriver(item.id) else openTeam(item.id)
        }
        val list = findViewById<RecyclerView>(R.id.rvStandings)
        list.layoutManager = LinearLayoutManager(this)
        list.addItemDecoration(ItemSpacing(resources.getDimensionPixelSize(R.dimen.spacing_sm)))
        list.adapter = adapter

        val tabDrivers = findViewById<TextView>(R.id.tabDrivers)
        val tabTeams = findViewById<TextView>(R.id.tabTeams)
        tabDrivers.setOnClickListener { viewModel.selectTab(drivers = true) }
        tabTeams.setOnClickListener { viewModel.selectTab(drivers = false) }

        val error = findViewById<TextView>(R.id.tvError)
        error.setOnClickListener { viewModel.load() }

        // Ekran samo posmatra: koji je tab izabran i sta treba prikazati
        viewModel.showDrivers.observe(this) { drivers ->
            styleTab(tabDrivers, active = drivers)
            styleTab(tabTeams, active = !drivers)
        }
        viewModel.state.observe(this) { state ->
            renderListState(
                state = state,
                list = list,
                progress = findViewById(R.id.progress),
                empty = findViewById(R.id.tvEmpty),
                error = error
            ) { items -> adapter.setItems(items) }
        }
    }

    /** Kad se vratimo sa ekrana Vozac, osvezi zvezdicu omiljenog vozaca. */
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    private fun styleTab(tab: TextView, active: Boolean) {
        tab.setBackgroundResource(if (active) R.drawable.bg_tab_active else R.drawable.bg_card)
        tab.setTextColor(ContextCompat.getColor(this, if (active) R.color.white else R.color.ink))
    }
}
