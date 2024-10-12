@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.ui.finished

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nadzirakarimantika.dicodingevent.data.EventRepository
import com.nadzirakarimantika.dicodingevent.data.Result
import com.nadzirakarimantika.dicodingevent.data.local.entity.EventEntity
import kotlinx.coroutines.launch

class FinishedViewModel(private val repository: EventRepository) : ViewModel() {

    private val _listEvents = MutableLiveData<List<EventEntity>>()
    val listEvents: LiveData<List<EventEntity>> = _listEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _showToastMessage = MutableLiveData<String?>()
    val showToastMessage: LiveData<String?> = _showToastMessage

    fun findFinishedEvent() {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getFinishedEvents().observeForever { result ->
                handleResult(result)
            }
        }
    }

    fun searchFinishedEvents(query: String) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.searchUpcomingEvents(query).observeForever { result ->
                handleResult(result)
            }
        }
    }

    private fun handleResult(result: Result<List<EventEntity>>) {
        when (result) {
            is Result.Loading -> {
                _isLoading.value = true
            }
            is Result.Success -> {
                _isLoading.value = false
                _listEvents.value = result.data
            }
            is Result.Error -> {
                _isLoading.value = false
            }
        }
    }

    fun clearToastMessage() {
        _showToastMessage.value = null
    }
}

