package com.example.formula1

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.Rect
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

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
        val color = ContextCompat.getColor(this, if (isActive) R.color.accent_light else R.color.muted)
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
fun Activity.bindDriverRow(
    rowId: Int,
    position: Int,
    nameRes: Int,
    subtitleRes: Int,
    opensDetail: Boolean = true
) {
    val row = findViewById<View>(rowId)
    val positionView = row.findViewById<TextView>(R.id.tvPosition)
    positionView.text = getString(R.string.position_format, position)
    positionView.setTextColor(ContextCompat.getColor(this, positionColorRes(position)))
    row.findViewById<TextView>(R.id.tvDriverName).text = getString(nameRes, position)
    row.findViewById<TextView>(R.id.tvDriverTeam).setText(subtitleRes)
    row.findViewById<TextView>(R.id.tvPoints).setText(R.string.points_placeholder)

    // Zvezdica uz omiljenog vozaca (samo u listi vozaca)
    val isFavorite = opensDetail && Favorites.get(this) == position
    row.findViewById<View>(R.id.ivFavorite).visibility = if (isFavorite) View.VISIBLE else View.GONE

    // Klik na vozaca -> ekran Vozac (za tab Timovi nema detalja)
    if (opensDetail) {
        row.setOnClickListener {
            val intent = Intent(this, DriverDetailActivity::class.java)
            intent.putExtra(DriverDetailActivity.EXTRA_DRIVER_POSITION, position)
            startActivity(intent)
        }
    } else {
        row.setOnClickListener(null)
        row.isClickable = false
    }
}

/** Boja pozicije u poretku: 1. mesto akcent, 2. i 3. bela, ostali prigusena. */
fun positionColorRes(position: Int): Int = when (position) {
    1 -> R.color.accent_light
    2, 3 -> R.color.ink
    else -> R.color.muted
}

/** Ako je lista prazna, sakrije listu i prikaze poruku umesto praznog ekrana. */
fun showEmptyMessage(list: View, emptyMessage: View, itemCount: Int) {
    val isEmpty = itemCount == 0
    list.visibility = if (isEmpty) View.GONE else View.VISIBLE
    emptyMessage.visibility = if (isEmpty) View.VISIBLE else View.GONE
}

/** Razmak izmedju stavki u RecyclerView-u. */
class ItemSpacing(private val spacePx: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.top = spacePx
        }
    }
}
