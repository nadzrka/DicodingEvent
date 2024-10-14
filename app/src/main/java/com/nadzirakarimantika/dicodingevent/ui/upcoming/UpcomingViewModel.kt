@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.ui.upcoming

import androidx.lifecycle.ViewModel
import com.nadzirakarimantika.dicodingevent.data.EventRepository

class UpcomingViewModel(private val eventRepository: EventRepository) : ViewModel() {

    fun findUpcomingEvents() = eventRepository.getUpcomingEvents()
    fun searchFinishedEvents(query: String) = eventRepository.searchUpcomingEvents(query)
}
