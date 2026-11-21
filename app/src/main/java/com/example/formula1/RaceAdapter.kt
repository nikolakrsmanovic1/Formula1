package com.example.formula1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.formula1.viewmodel.RaceItem

/**
 * Adapter za listu trka. Za svaku trku pravi jedan red iz layouta item_race
 * i popunjava ga podacima; klik na red javlja Activity-ju koja je trka izabrana.
 */
class RaceAdapter(
    private val onRaceClick: (RaceItem) -> Unit
) : RecyclerView.Adapter<RaceAdapter.RaceViewHolder>() {

    private var races: List<RaceItem> = emptyList()

    /** ViewHolder drzi reference na View-e jednog reda da se ne traze iznova pri skrolovanju. */
    class RaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val round: TextView = view.findViewById(R.id.tvRaceRound)
        val name: TextView = view.findViewById(R.id.tvRaceName)
        val place: TextView = view.findViewById(R.id.tvRacePlace)
        val date: TextView = view.findViewById(R.id.tvRaceDate)
    }

    /** Menja prikazanu listu kad stignu podaci i osvezava ekran. */
    fun setItems(newItems: List<RaceItem>) {
        races = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_race, parent, false)
        return RaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: RaceViewHolder, position: Int) {
        val race = races[position]
        holder.round.text = holder.itemView.context.getString(R.string.race_round_format, race.round)
        holder.name.text = race.name
        holder.place.text = race.place
        holder.date.text = race.date
        holder.itemView.setOnClickListener { onRaceClick(race) }
    }

    override fun getItemCount(): Int = races.size
}
