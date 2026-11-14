package com.example.formula1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.formula1.databinding.ActivityProfileBinding
import com.example.formula1.databinding.ItemSettingBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyInsets(binding.root)

        // Strelica nazad -> vraća na Početnu
        binding.btnBack.setOnClickListener { finish() }

        bindSetting(binding.settingRow1, R.string.setting_theme, R.string.setting_theme_value)
        bindSetting(binding.settingRow2, R.string.setting_language, R.string.setting_language_value)
        bindSetting(
            binding.settingRow3,
            R.string.setting_notifications,
            R.string.setting_notifications_value
        )

        // Omiljeni vozac: otvara ga ako je izabran, a ako nije vodi na Poredak
        binding.settingRow4.root.setOnClickListener {
            val favorite = Favorites.get(this)
            if (favorite == Favorites.NONE) {
                startActivity(Intent(this, StandingsActivity::class.java))
            } else {
                val intent = Intent(this, DriverDetailActivity::class.java)
                intent.putExtra(DriverDetailActivity.EXTRA_DRIVER_POSITION, favorite)
                startActivity(intent)
            }
        }

        // Odjavi se -> Prijava (briše istoriju ekrana)
        binding.btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    /** Osvezava red Omiljeni vozac kad se vratimo sa drugog ekrana. */
    override fun onResume() {
        super.onResume()
        val row = binding.settingRow4
        row.tvSettingTitle.setText(R.string.setting_favorite)
        val favorite = Favorites.get(this)
        row.tvSettingValue.text =
            if (favorite == Favorites.NONE) getString(R.string.setting_favorite_none)
            else getString(R.string.driver_name_format, favorite)
    }

    private fun bindSetting(row: ItemSettingBinding, titleRes: Int, valueRes: Int) {
        row.tvSettingTitle.setText(titleRes)
        row.tvSettingValue.setText(valueRes)
        row.root.setOnClickListener {
            Toast.makeText(this, R.string.toast_soon, Toast.LENGTH_SHORT).show()
        }
    }
}
