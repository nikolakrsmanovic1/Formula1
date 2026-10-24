package com.example.formula1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        applyInsets()

        // Strelica nazad -> vraća na Početnu
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        bindSetting(R.id.settingRow1, R.string.setting_theme, R.string.setting_theme_value)
        bindSetting(R.id.settingRow2, R.string.setting_language, R.string.setting_language_value)
        bindSetting(
            R.id.settingRow3,
            R.string.setting_notifications,
            R.string.setting_notifications_value
        )

        // Odjavi se -> Prijava (briše istoriju ekrana)
        findViewById<View>(R.id.btnLogout).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun bindSetting(rowId: Int, titleRes: Int, valueRes: Int) {
        val row = findViewById<View>(rowId)
        row.findViewById<TextView>(R.id.tvSettingTitle).setText(titleRes)
        row.findViewById<TextView>(R.id.tvSettingValue).setText(valueRes)
        row.setOnClickListener {
            Toast.makeText(this, R.string.toast_soon, Toast.LENGTH_SHORT).show()
        }
    }
}
