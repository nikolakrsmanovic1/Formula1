package com.example.formula1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Rect
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.formula1.model.ErrorKind
import com.example.formula1.viewmodel.StandingItem
import com.example.formula1.viewmodel.UiState

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

// --- Prikaz stanja (UiState) ---

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

/** Poruka za gresku, na osnovu vrste greske koju javi ViewModel. */
fun errorMessageRes(kind: ErrorKind): Int = when (kind) {
    ErrorKind.NETWORK -> R.string.error_network
    ErrorKind.SERVER -> R.string.error_server
    ErrorKind.UNKNOWN -> R.string.error_unknown
}

/**
 * Prikazuje samo onaj deo ekrana koji odgovara stanju: spiner, listu, poruku o praznoj listi
 * ili poruku o gresci. Kad stignu podaci, poziva onSuccess.
 */
fun <T> renderListState(
    state: UiState<T>,
    list: View,
    progress: View,
    empty: View,
    error: TextView,
    onSuccess: (T) -> Unit
) {
    list.setVisible(state is UiState.Success)
    progress.setVisible(state is UiState.Loading)
    empty.setVisible(state is UiState.Empty)
    error.setVisible(state is UiState.Error)
    when (state) {
        is UiState.Success -> onSuccess(state.data)
        is UiState.Error -> error.setText(errorMessageRes(state.kind))
        is UiState.Loading, is UiState.Empty -> Unit
    }
}

// --- Prelazak na ekrane sa detaljima ---

fun Activity.openDriver(driverId: String) {
    val intent = Intent(this, DriverDetailActivity::class.java)
    intent.putExtra(DriverDetailActivity.EXTRA_DRIVER_ID, driverId)
    startActivity(intent)
}

fun Activity.openTeam(teamId: String) {
    val intent = Intent(this, TeamDetailActivity::class.java)
    intent.putExtra(TeamDetailActivity.EXTRA_TEAM_ID, teamId)
    startActivity(intent)
}

// --- Redovi poretka ---

fun positionText(context: Context, position: Int): String =
    if (position > 0) context.getString(R.string.position_format, position)
    else context.getString(R.string.stat_value_placeholder)

fun pointsText(context: Context, points: String): String =
    if (points.isBlank()) "" else context.getString(R.string.points_format, points)

/** Boja pozicije u poretku: 1. mesto akcent, 2. i 3. bela, ostali prigusena. */
fun positionColorRes(position: Int): Int = when (position) {
    1 -> R.color.accent_light
    2, 3 -> R.color.ink
    else -> R.color.muted
}

/** Popunjava jedan red iz layouta item_driver (koji je ubacen preko include). Prazan id = red bez klika. */
fun Activity.bindDriverRow(rowId: Int, item: StandingItem) {
    val row = findViewById<View>(rowId)
    val positionView = row.findViewById<TextView>(R.id.tvPosition)
    positionView.text = positionText(this, item.position)
    positionView.setTextColor(ContextCompat.getColor(this, positionColorRes(item.position)))
    row.findViewById<TextView>(R.id.tvDriverName).text = item.title
    row.findViewById<TextView>(R.id.tvDriverTeam).text = item.subtitle
    row.findViewById<TextView>(R.id.tvPoints).text = pointsText(this, item.points)
    row.findViewById<View>(R.id.ivFavorite)
        .setVisible(item.isDriver && Favorites.getId(this) == item.id)

    if (item.id.isEmpty()) {
        row.setOnClickListener(null)
        row.isClickable = false
    } else {
        row.setOnClickListener {
            if (item.isDriver) openDriver(item.id) else openTeam(item.id)
        }
    }
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
