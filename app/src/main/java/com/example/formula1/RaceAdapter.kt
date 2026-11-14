package com.example.formula1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.formula1.databinding.ItemRaceBinding

/**
 * Adapter za listu trka. Za svaku trku pravi jedan red iz layouta item_race
 * i popunjava ga podacima; klik na red javlja Activity-ju koja je trka izabrana.
 */
class RaceAdapter(
    private val races: List<Race>,
    private val onRaceClick: (Race) -> Unit
) : RecyclerView.Adapter<RaceAdapter.RaceViewHolder>() {

    /** ViewHolder drzi binding jednog reda, pa se View-i ne traze iznova pri skrolovanju. */
    class RaceViewHolder(val binding: ItemRaceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaceViewHolder {
        val binding = ItemRaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RaceViewHolder, position: Int) {
        val race = races[position]
        val row = holder.binding
        row.tvRaceRound.text = row.root.context.getString(R.string.race_round_format, race.number)
        row.tvRaceName.text = race.name
        row.tvRacePlace.text = race.place
        row.tvRaceDate.text = race.date
        row.root.setOnClickListener { onRaceClick(race) }
    }

    override fun getItemCount(): Int = races.size
}
