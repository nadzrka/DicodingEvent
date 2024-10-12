@file:Suppress("unused", "RedundantSuppression")
package com.nadzirakarimantika.dicodingevent.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nadzirakarimantika.dicodingevent.data.remote.response.ListEventsItem
import com.nadzirakarimantika.dicodingevent.databinding.ItemRowEventBinding

class EventAdapter(
    private var events: List<ListEventsItem>,
    private val onItemClick: (ListEventsItem) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(private val binding: ItemRowEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            binding.tvItemName.text = event.name

            Glide.with(itemView.context)
                .load(event.mediaCover)
                .into(binding.imgItemPhoto)

            // Set the click listener
            itemView.setOnClickListener {
                onItemClick(event)
            }
        }
    }

    fun updateEvents(newEvents: List<ListEventsItem>) {
        events = newEvents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): EventViewHolder {
        val binding = ItemRowEventBinding.inflate(LayoutInflater.from(p0.context), p0, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size
}
