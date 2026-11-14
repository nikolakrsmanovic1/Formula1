package com.example.formula1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.formula1.databinding.ActivityScheduleBinding

class ScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScheduleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyInsets(binding.root)
        setupBottomNav(binding.bottomNav, NavTab.SCHEDULE)

        binding.btnFilter.setOnClickListener {
            Toast.makeText(this, R.string.toast_soon, Toast.LENGTH_SHORT).show()
        }

        // Privremeni podaci (kasnije iz API-ja): trke 1..race_count
        val races = List(resources.getInteger(R.integer.race_count)) { index ->
            Race(
                number = index + 1,
                name = getString(R.string.race_name_placeholder),
                place = getString(R.string.race_place_placeholder),
                date = getString(R.string.race_date_placeholder)
            )
        }

        // RecyclerView: LayoutManager rasporedjuje stavke, Adapter ih pravi i popunjava
        binding.rvRaces.layoutManager = LinearLayoutManager(this)
        binding.rvRaces.addItemDecoration(
            ItemSpacing(resources.getDimensionPixelSize(R.dimen.spacing_sm))
        )
        binding.rvRaces.adapter = RaceAdapter(races) { race ->
            // Klik na trku -> Detalj trke (salje broj trke preko Intent-a)
            val intent = Intent(this, RaceDetailActivity::class.java)
            intent.putExtra(RaceDetailActivity.EXTRA_RACE_NUMBER, race.number)
            startActivity(intent)
        }

        // Prazna lista -> poruka umesto praznog ekrana
        showEmptyMessage(binding.rvRaces, binding.tvEmpty, races.size)
    }
}
