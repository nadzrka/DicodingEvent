@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.ui.favorite

import androidx.lifecycle.ViewModel
import com.nadzirakarimantika.dicodingevent.data.EventRepository

class FavoriteViewModel(private val repository: EventRepository) : ViewModel() {

    fun findFavoriteEvent() = repository.getFavoriteEvents()

}

