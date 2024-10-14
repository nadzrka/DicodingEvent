@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.ui.home

import androidx.lifecycle.ViewModel
import com.nadzirakarimantika.dicodingevent.data.EventRepository

class HomeViewModel(private val repository: EventRepository) : ViewModel() {

    fun getFinishedEvents() = repository.getFinishedEvents()
    fun getUpcomingEvents() = repository.getUpcomingEvents()
    fun searchUpcomingEvents(query: String) = repository.searchUpcomingEvents(query)
    fun searchFinishedEvents(query: String) = repository.searchFinishedEvents(query)
}
