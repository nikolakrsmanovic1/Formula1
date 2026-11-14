package com.example.formula1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.formula1.databinding.ActivityHomeBinding
import com.example.formula1.databinding.ItemCountdownBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyInsets(binding.root)
        setupBottomNav(binding.bottomNav, NavTab.HOME)

        // Ikonica korisnika -> Profil
        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Odbrojavanje (privremeni podaci, kasnije iz API-ja)
        bindCountdown(binding.cdDays, R.string.cd_days_value, R.string.cd_days_label)
        bindCountdown(binding.cdHours, R.string.cd_hours_value, R.string.cd_hours_label)
        bindCountdown(binding.cdMinutes, R.string.cd_minutes_value, R.string.cd_minutes_label)
        bindCountdown(binding.cdSeconds, R.string.cd_seconds_value, R.string.cd_seconds_label)

        // Kartica omiljenog vozaca bez izbora -> Poredak, gde se vozac bira
        binding.tvFavEmpty.setOnClickListener {
            startActivity(Intent(this, StandingsActivity::class.java))
        }
    }

    /** Osvezava omiljenog vozaca i Top 3 svaki put kad se vratimo na Pocetnu. */
    override fun onResume() {
        super.onResume()
        bindFavorite()
        listOf(binding.driverRow1, binding.driverRow2, binding.driverRow3)
            .forEachIndexed { index, row ->
                bindDriverRow(row, index + 1, R.string.driver_name_format, R.string.team_placeholder)
            }
    }

    private fun bindFavorite() {
        val favorite = Favorites.get(this)
        val hasFavorite = favorite != Favorites.NONE
        binding.favRow.root.visibility = if (hasFavorite) android.view.View.VISIBLE else android.view.View.GONE
        binding.tvFavEmpty.visibility = if (hasFavorite) android.view.View.GONE else android.view.View.VISIBLE
        if (hasFavorite) {
            bindDriverRow(binding.favRow, favorite, R.string.driver_name_format, R.string.team_placeholder)
        }
    }

    private fun bindCountdown(box: ItemCountdownBinding, valueRes: Int, labelRes: Int) {
        box.tvCdValue.setText(valueRes)
        box.tvCdLabel.setText(labelRes)
    }
}
