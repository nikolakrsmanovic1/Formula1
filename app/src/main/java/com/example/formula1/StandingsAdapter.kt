package com.example.formula1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.formula1.viewmodel.StandingItem

/**
 * Adapter za poredak. Isti adapter prikazuje i vozace i timove (tab Vozaci / Timovi),
 * samo mu se zameni lista preko setItems().
 */
class StandingsAdapter(
    private val onItemClick: (StandingItem) -> Unit
) : RecyclerView.Adapter<StandingsAdapter.StandingViewHolder>() {

    private var items: List<StandingItem> = emptyList()

    class StandingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val position: TextView = view.findViewById(R.id.tvPosition)
        val title: TextView = view.findViewById(R.id.tvDriverName)
        val subtitle: TextView = view.findViewById(R.id.tvDriverTeam)
        val points: TextView = view.findViewById(R.id.tvPoints)
        val favoriteStar: View = view.findViewById(R.id.ivFavorite)
    }

    /** Menja prikazanu listu (vozaci ili timovi) i osvezava ekran. */
    fun setItems(newItems: List<StandingItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StandingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_driver, parent, false)
        return StandingViewHolder(view)
    }

    override fun onBindViewHolder(holder: StandingViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context
        holder.position.text = positionText(context, item.position)
        holder.position.setTextColor(ContextCompat.getColor(context, positionColorRes(item.position)))
        holder.title.text = item.title
        holder.subtitle.text = item.subtitle
        holder.points.text = pointsText(context, item.points)

        // Zvezdica samo uz omiljenog vozaca (ne u listi timova)
        holder.favoriteStar.setVisible(item.isDriver && Favorites.getId(context) == item.id)

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size
}
