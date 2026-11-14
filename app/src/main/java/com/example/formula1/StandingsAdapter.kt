package com.example.formula1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.formula1.databinding.ItemDriverBinding

/**
 * Adapter za poredak. Isti adapter prikazuje i vozace i timove (tab Vozaci / Timovi),
 * samo mu se zameni lista preko setItems().
 */
class StandingsAdapter(
    private val onItemClick: (StandingItem) -> Unit
) : RecyclerView.Adapter<StandingsAdapter.StandingViewHolder>() {

    private var items: List<StandingItem> = emptyList()
    private var isDriverList = true

    class StandingViewHolder(val binding: ItemDriverBinding) : RecyclerView.ViewHolder(binding.root)

    /** Menja prikazanu listu (vozaci ili timovi) i osvezava ekran. */
    fun setItems(newItems: List<StandingItem>, driverList: Boolean) {
        items = newItems
        isDriverList = driverList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StandingViewHolder {
        val binding = ItemDriverBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StandingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StandingViewHolder, position: Int) {
        val item = items[position]
        val row = holder.binding
        val context = row.root.context

        row.tvPosition.text = context.getString(R.string.position_format, item.position)
        row.tvPosition.setTextColor(ContextCompat.getColor(context, positionColorRes(item.position)))
        row.tvDriverName.text = item.title
        row.tvDriverTeam.text = item.subtitle
        row.tvPoints.text = item.points

        // Zvezdica samo uz omiljenog vozaca (ne u listi timova)
        val isFavorite = isDriverList && Favorites.get(context) == item.position
        row.ivFavorite.visibility = if (isFavorite) View.VISIBLE else View.GONE

        row.root.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size
}
