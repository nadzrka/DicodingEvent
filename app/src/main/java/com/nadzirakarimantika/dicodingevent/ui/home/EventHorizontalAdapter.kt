@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nadzirakarimantika.dicodingevent.data.remote.response.ListEventsItem
import com.nadzirakarimantika.dicodingevent.databinding.ItemEventHorizontalBinding

class EventHorizontalAdapter(
    private var events: List<ListEventsItem>,
    private val onItemClick: (ListEventsItem) -> Unit
) : RecyclerView.Adapter<EventHorizontalAdapter.EventViewHolder>() {

    inner class EventViewHolder(private val binding: ItemEventHorizontalBinding, val onItemClick: (ListEventsItem) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: ListEventsItem) {
            binding.eventTitle.text =  HtmlCompat.fromHtml(
                event.name.toString(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            Glide.with(itemView.context)
                .load(event.imageLogo)
                .into(binding.eventPhoto)

            itemView.setOnClickListener {
                onItemClick(event)
            }
        }
    }

    fun updateEvents(newEvents: List<ListEventsItem>?) {
        events = newEvents ?: emptyList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int = events.take(5).size

}
