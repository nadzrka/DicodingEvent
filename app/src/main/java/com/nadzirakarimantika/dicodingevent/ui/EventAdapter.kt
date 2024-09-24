package com.nadzirakarimantika.dicodingevent.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nadzirakarimantika.dicodingevent.data.response.ListEventsItem
import com.nadzirakarimantika.dicodingevent.databinding.ItemRowEventBinding

class EventAdapter(private val events: List<ListEventsItem>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(private val binding: ItemRowEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            binding.tvItemName.text = event.name

            Glide.with(itemView.context)
                .load(event.mediaCover)
                .into(binding.imgItemPhoto)
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): EventViewHolder {
        val binding = ItemRowEventBinding.inflate(LayoutInflater.from(p0.context), p0, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount(): Int {
        return events.size
    }
}