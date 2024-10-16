@file:Suppress("unused", "RedundantSuppression")
package com.nadzirakarimantika.dicodingevent.ui

import androidx.lifecycle.ViewModel
import com.nadzirakarimantika.dicodingevent.data.EventRepository
import com.nadzirakarimantika.dicodingevent.data.local.entity.EventEntity

class DetailViewModel (private val repository: EventRepository) : ViewModel() {

    fun getDetailEvent(query: String) = repository.getDetailEvent(query)
    fun addFavorite() = repository.addFavoriteEvent()
    fun getFavorite() = repository.getFavoriteEvents()

}