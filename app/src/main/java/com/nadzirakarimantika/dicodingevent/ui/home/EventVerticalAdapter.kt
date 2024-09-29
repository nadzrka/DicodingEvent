package com.nadzirakarimantika.dicodingevent.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nadzirakarimantika.dicodingevent.data.response.ListEventsItem
import com.nadzirakarimantika.dicodingevent.databinding.ItemEventVerticalBinding

class EventVerticalAdapter(
    private var events: List<ListEventsItem>,
    private val onItemClick: (ListEventsItem) -> Unit
) : RecyclerView.Adapter<EventVerticalAdapter.EventViewHolder>() {

    inner class EventViewHolder(private val binding: ItemEventVerticalBinding, val onItemClick: (ListEventsItem) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: ListEventsItem) {

            binding.tvItemName.text =  HtmlCompat.fromHtml(
                event.name.toString(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            Glide.with(itemView.context)
                .load(event.imageLogo)
                .into(binding.imgItemPhoto)

            itemView.setOnClickListener {
                onItemClick(event)
            }
        }
    }

    fun updateEvents(newEvents: List<ListEventsItem>) {
        events = newEvents
        notifyDataSetChanged() // Refresh the RecyclerView with new data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int = events.take(5).size
}
