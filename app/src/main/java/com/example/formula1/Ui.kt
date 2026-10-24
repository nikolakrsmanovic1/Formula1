package com.example.formula1

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/** Tri glavna odeljka koja imaju donju navigaciju. */
enum class NavTab { HOME, SCHEDULE, STANDINGS }

/** Dodaje razmak ispod status bara i iznad navigacionog bara (potrebno na novijem Androidu). */
fun Activity.applyInsets() {
    val root = findViewById<View>(R.id.root)
    ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
        insets
    }
}

/** Povezuje donju navigaciju: oznaci aktivni tab i otvara ostale ekrane preko Intent-a. */
fun Activity.setupBottomNav(active: NavTab) {
    val tabs = listOf<Triple<Int, NavTab, Class<*>>>(
        Triple(R.id.navHome, NavTab.HOME, HomeActivity::class.java),
        Triple(R.id.navSchedule, NavTab.SCHEDULE, ScheduleActivity::class.java),
        Triple(R.id.navStandings, NavTab.STANDINGS, StandingsActivity::class.java)
    )
    for ((id, tab, target) in tabs) {
        val item = findViewById<TextView>(id)
        val isActive = tab == active
        val color = ContextCompat.getColor(this, if (isActive) R.color.ink else R.color.muted)
        item.setTextColor(color)
        item.compoundDrawableTintList = ColorStateList.valueOf(color)
        item.setTypeface(null, if (isActive) Typeface.BOLD else Typeface.NORMAL)
        item.setOnClickListener {
            if (!isActive) {
                startActivity(Intent(this, target))
                finish()
            }
        }
    }
}

/** Popunjava jedan red liste vozaca (ili timova). */
fun Activity.bindDriverRow(rowId: Int, position: Int, nameRes: Int, subtitleRes: Int) {
    val row = findViewById<View>(rowId)
    row.findViewById<TextView>(R.id.tvPosition).text = getString(R.string.position_format, position)
    row.findViewById<TextView>(R.id.tvDriverName).text = getString(nameRes, position)
    row.findViewById<TextView>(R.id.tvDriverTeam).setText(subtitleRes)
    row.findViewById<TextView>(R.id.tvPoints).setText(R.string.points_placeholder)
}
