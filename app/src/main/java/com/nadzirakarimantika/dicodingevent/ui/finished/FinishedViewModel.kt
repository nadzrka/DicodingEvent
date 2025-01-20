@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.ui.finished

import androidx.lifecycle.ViewModel
import com.nadzirakarimantika.dicodingevent.data.EventRepository

class FinishedViewModel(private val repository: EventRepository) : ViewModel() {

    fun findFinishedEvent() = repository.getFinishedEvents()
    fun searchFinishedEvents(query: String) = repository.searchEvents(query, isFinished = true)

}

