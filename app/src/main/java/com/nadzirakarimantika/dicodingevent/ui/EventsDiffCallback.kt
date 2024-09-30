package com.nadzirakarimantika.dicodingevent.ui

import androidx.recyclerview.widget.DiffUtil
import com.nadzirakarimantika.dicodingevent.data.response.ListEventsItem

class EventsDiffCallback(
    private val oldList: List<ListEventsItem>,
    private val newList: List<ListEventsItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
        // Compare unique identifiers (e.g., id)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
        // Compare full contents of the item
    }
}
