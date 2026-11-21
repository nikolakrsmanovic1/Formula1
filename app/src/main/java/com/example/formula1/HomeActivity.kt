package com.example.formula1

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.formula1.data.ServiceLocator
import com.example.formula1.viewmodel.HomeViewModel
import com.example.formula1.viewmodel.NextRaceInfo
import com.example.formula1.viewmodel.StandingItem
import com.example.formula1.viewmodel.UiState
import com.example.formula1.viewmodel.ViewModelFactory

class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory { HomeViewModel(ServiceLocator.repository) }
    }

    private val countdownBoxes = intArrayOf(R.id.cdDays, R.id.cdHours, R.id.cdMinutes, R.id.cdSeconds)
    private val topRows = intArrayOf(R.id.driverRow1, R.id.driverRow2, R.id.driverRow3)

    private var countdown: CountDownTimer? = null
    private var targetMillis: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        applyInsets()
        setupBottomNav(NavTab.HOME)

        // Ikonica korisnika -> Profil
        findViewById<View>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Oznake odbrojavanja (vrednosti dolaze kasnije, kad se zna vreme trke)
        val labels = intArrayOf(
            R.string.cd_days_label, R.string.cd_hours_label,
            R.string.cd_minutes_label, R.string.cd_seconds_label
        )
        countdownBoxes.forEachIndexed { index, boxId ->
            findViewById<View>(boxId).findViewById<TextView>(R.id.tvCdLabel).setText(labels[index])
        }

        // Kartica sledece trke: ako ucitavanje ne uspe, dodir pokusava ponovo
        findViewById<View>(R.id.cardNextRace).setOnClickListener { viewModel.load() }

        // Kartica omiljenog vozaca bez izbora -> Poredak, gde se vozac bira
        findViewById<View>(R.id.tvFavEmpty).setOnClickListener {
            startActivity(Intent(this, StandingsActivity::class.java))
        }

        // Ekran samo posmatra stanje koje objavljuje ViewModel
        viewModel.nextRace.observe(this) { renderNextRace(it) }
        viewModel.top3.observe(this) { renderTop3(it) }
        viewModel.favorite.observe(this) { renderFavorite() }
    }

    override fun onResume() {
        super.onResume()
        // Omiljeni vozac se cuva u SharedPreferences, pa ga ViewModel-u javljamo pri svakom povratku
        viewModel.setFavoriteId(Favorites.getId(this))
    }

    override fun onStart() {
        super.onStart()
        startCountdown()
    }

    override fun onStop() {
        super.onStop()
        countdown?.cancel()
    }

    // --- Sledeca trka i odbrojavanje ---

    private fun renderNextRace(state: UiState<NextRaceInfo>) {
        val name = findViewById<TextView>(R.id.tvNextName)
        val place = findViewById<TextView>(R.id.tvNextPlace)
        targetMillis = null
        when (state) {
            is UiState.Loading -> {
                name.setText(R.string.loading)
                place.text = ""
            }
            is UiState.Success -> {
                val race = state.data
                name.text = race.name
                place.text = getString(R.string.next_race_place_format, race.locality, race.country, race.dateText)
                targetMillis = race.startMillis
            }
            is UiState.Empty -> {
                name.setText(R.string.next_race_season_over)
                place.text = ""
            }
            is UiState.Error -> {
                name.setText(errorMessageRes(state.kind))
                place.text = ""
            }
        }
        startCountdown()
    }

    /** Pokrece odbrojavanje do trke; bez vremena trke prikazuje crtice. */
    private fun startCountdown() {
        countdown?.cancel()
        val target = targetMillis
        if (target == null) {
            showCountdown(null)
            return
        }
        val remaining = target - System.currentTimeMillis()
        if (remaining <= 0) {
            showCountdown(0L)
            return
        }
        countdown = object : CountDownTimer(remaining, TICK_MILLIS) {
            override fun onTick(millisUntilFinished: Long) = showCountdown(millisUntilFinished)
            override fun onFinish() = showCountdown(0L)
        }.start()
    }

    private fun showCountdown(millis: Long?) {
        val values = if (millis == null) {
            List(countdownBoxes.size) { getString(R.string.stat_value_placeholder) }
        } else {
            val totalSeconds = millis / MILLIS_IN_SECOND
            listOf(
                totalSeconds / SECONDS_IN_DAY,
                totalSeconds % SECONDS_IN_DAY / SECONDS_IN_HOUR,
                totalSeconds % SECONDS_IN_HOUR / SECONDS_IN_MINUTE,
                totalSeconds % SECONDS_IN_MINUTE
            ).map { getString(R.string.cd_value_format, it.toInt()) }
        }
        countdownBoxes.forEachIndexed { index, boxId ->
            findViewById<View>(boxId).findViewById<TextView>(R.id.tvCdValue).text = values[index]
        }
    }

    // --- Top 3 i omiljeni vozac ---

    private fun renderTop3(state: UiState<List<StandingItem>>) {
        val drivers = (state as? UiState.Success)?.data.orEmpty()
        topRows.forEachIndexed { index, rowId ->
            // Dok podaci ne stignu (ili ne uspe ucitavanje) red prikazuje crtice
            bindDriverRow(rowId, drivers.getOrNull(index) ?: placeholderRow(index + 1))
        }
    }

    private fun renderFavorite() {
        val id = Favorites.getId(this)
        val row = findViewById<View>(R.id.favRow)
        val empty = findViewById<View>(R.id.tvFavEmpty)
        row.setVisible(id != null)
        empty.setVisible(id == null)
        if (id == null) return

        // Ako podaci jos nisu stigli, prikazuje se sacuvano ime
        val item = viewModel.favorite.value?.takeIf { it.id == id }
            ?: StandingItem(id, 0, Favorites.getName(this).orEmpty(), "", "", true)
        bindDriverRow(R.id.favRow, item)
    }

    private fun placeholderRow(position: Int) =
        StandingItem("", position, getString(R.string.stat_value_placeholder), "", "", true)

    private companion object {
        const val TICK_MILLIS = 1000L
        const val MILLIS_IN_SECOND = 1000L
        const val SECONDS_IN_MINUTE = 60L
        const val SECONDS_IN_HOUR = 3600L
        const val SECONDS_IN_DAY = 86400L
    }
}
