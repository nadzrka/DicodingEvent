@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nadzirakarimantika.dicodingevent.R
import com.nadzirakarimantika.dicodingevent.data.local.entity.EventEntity
import com.nadzirakarimantika.dicodingevent.databinding.ItemEventVerticalBinding

class EventVerticalAdapter(
    private val onItemClick: (EventEntity) -> Unit
) : ListAdapter<EventEntity, EventVerticalAdapter.EventViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }

    class EventViewHolder(
        private val binding: ItemEventVerticalBinding,
        private val onItemClick: (EventEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: EventEntity) {
            binding.eventName.text =  HtmlCompat.fromHtml(
                event.name,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            Glide.with(itemView.context)
                .load(event.imageLogo)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.placeholder_square)
                        .error(R.drawable.placeholder_square)
                        .fitCenter()
                )
                .into(binding.imgItemPhoto)

            itemView.setOnClickListener {
                onItemClick(event)
            }
        }
    }

    override fun getItemCount(): Int = currentList.size

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<EventEntity> =
            object : DiffUtil.ItemCallback<EventEntity>() {
                override fun areItemsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }

}
