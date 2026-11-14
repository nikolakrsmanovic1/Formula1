package com.example.formula1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.formula1.databinding.ActivityDriverDetailBinding

class DriverDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDriverDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyInsets(binding.root)

        // Pozicija vozača stiže preko Intent-a iz Početne ili Poretka
        val position = intent.getIntExtra(EXTRA_DRIVER_POSITION, 1)
        binding.tvDriverName.text = getString(R.string.driver_name_format, position)
        binding.tvDriverNumber.text = getString(R.string.driver_number_format, position)

        // Strelica nazad -> vraća na prethodni ekran
        binding.btnBack.setOnClickListener { finish() }

        // Zvezdica -> dodaj ili ukloni iz omiljenih (cuva se samo jedan omiljeni vozac)
        showFavorite(position)
        binding.btnFavorite.setOnClickListener {
            if (Favorites.get(this) == position) {
                Favorites.clear(this)
                Toast.makeText(this, R.string.toast_favorite_removed, Toast.LENGTH_SHORT).show()
            } else {
                Favorites.set(this, position)
                Toast.makeText(this, R.string.toast_favorite_added, Toast.LENGTH_SHORT).show()
            }
            showFavorite(position)
        }

        // Kartice statistike (privremeni podaci, kasnije iz API-ja)
        val stats = listOf(
            binding.stat1 to R.string.stat_wins,
            binding.stat2 to R.string.stat_poles,
            binding.stat3 to R.string.stat_podiums,
            binding.stat4 to R.string.stat_dnf,
            binding.stat5 to R.string.stat_fastest_laps,
            binding.stat6 to R.string.stat_titles,
            binding.stat7 to R.string.stat_races,
            binding.stat8 to R.string.stat_points
        )
        for ((card, labelRes) in stats) {
            card.tvStatValue.setText(R.string.stat_value_placeholder)
            card.tvStatLabel.setText(labelRes)
        }
    }

    private fun showFavorite(position: Int) {
        val isFavorite = Favorites.get(this) == position
        binding.btnFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star
        )
    }

    companion object {
        const val EXTRA_DRIVER_POSITION = "driver_position"
    }
}
