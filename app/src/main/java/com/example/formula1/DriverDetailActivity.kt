package com.example.formula1

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DriverDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_detail)
        applyInsets()

        // Pozicija vozača stiže preko Intent-a iz Početne ili Poretka
        val position = intent.getIntExtra(EXTRA_DRIVER_POSITION, 1)
        findViewById<TextView>(R.id.tvDriverName).text =
            getString(R.string.driver_name_format, position)
        findViewById<TextView>(R.id.tvDriverNumber).text =
            getString(R.string.driver_number_format, position)

        // Strelica nazad -> vraća na prethodni ekran
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        // Zvezdica -> dodaj ili ukloni iz omiljenih (cuva se samo jedan omiljeni vozac)
        val btnFavorite = findViewById<ImageButton>(R.id.btnFavorite)
        showFavorite(btnFavorite, position)
        btnFavorite.setOnClickListener {
            if (Favorites.get(this) == position) {
                Favorites.clear(this)
                Toast.makeText(this, R.string.toast_favorite_removed, Toast.LENGTH_SHORT).show()
            } else {
                Favorites.set(this, position)
                Toast.makeText(this, R.string.toast_favorite_added, Toast.LENGTH_SHORT).show()
            }
            showFavorite(btnFavorite, position)
        }

        // Kartice statistike (privremeni podaci, kasnije iz API-ja)
        val stats = listOf(
            R.id.stat1 to R.string.stat_wins,
            R.id.stat2 to R.string.stat_poles,
            R.id.stat3 to R.string.stat_podiums,
            R.id.stat4 to R.string.stat_dnf,
            R.id.stat5 to R.string.stat_fastest_laps,
            R.id.stat6 to R.string.stat_titles,
            R.id.stat7 to R.string.stat_races,
            R.id.stat8 to R.string.stat_points
        )
        for ((id, labelRes) in stats) {
            val card = findViewById<View>(id)
            card.findViewById<TextView>(R.id.tvStatValue).setText(R.string.stat_value_placeholder)
            card.findViewById<TextView>(R.id.tvStatLabel).setText(labelRes)
        }
    }

    private fun showFavorite(button: ImageButton, position: Int) {
        val isFavorite = Favorites.get(this) == position
        button.setImageResource(if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star)
    }

    companion object {
        const val EXTRA_DRIVER_POSITION = "driver_position"
    }
}
