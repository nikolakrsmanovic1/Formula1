package com.example.formula1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.formula1.data.ServiceLocator
import com.example.formula1.viewmodel.ScheduleViewModel
import com.example.formula1.viewmodel.ViewModelFactory

class ScheduleActivity : AppCompatActivity() {

    // ViewModel drzi stanje ekrana i prezivljava rotaciju; podatke trazi od Repository-ja
    private val viewModel: ScheduleViewModel by viewModels {
        ViewModelFactory { ScheduleViewModel(ServiceLocator.repository) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        applyInsets()
        setupBottomNav(NavTab.SCHEDULE)

        findViewById<View>(R.id.btnFilter).setOnClickListener {
            Toast.makeText(this, R.string.toast_soon, Toast.LENGTH_SHORT).show()
        }

        // RecyclerView: LayoutManager rasporedjuje stavke, Adapter ih pravi i popunjava
        val adapter = RaceAdapter { race ->
            // Klik na trku -> Detalj trke (salje broj runde preko Intent-a)
            val intent = Intent(this, RaceDetailActivity::class.java)
            intent.putExtra(RaceDetailActivity.EXTRA_RACE_NUMBER, race.round)
            startActivity(intent)
        }
        val list = findViewById<RecyclerView>(R.id.rvRaces)
        list.layoutManager = LinearLayoutManager(this)
        list.addItemDecoration(ItemSpacing(resources.getDimensionPixelSize(R.dimen.spacing_sm)))
        list.adapter = adapter

        val error = findViewById<TextView>(R.id.tvError)
        error.setOnClickListener { viewModel.load() }

        // Ekran samo posmatra stanje i prikazuje ga
        viewModel.state.observe(this) { state ->
            renderListState(
                state = state,
                list = list,
                progress = findViewById(R.id.progress),
                empty = findViewById(R.id.tvEmpty),
                error = error
            ) { races -> adapter.setItems(races) }
        }
    }
}
