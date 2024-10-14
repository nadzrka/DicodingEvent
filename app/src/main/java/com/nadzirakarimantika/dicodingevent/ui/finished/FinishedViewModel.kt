@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.ui.finished

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nadzirakarimantika.dicodingevent.data.FinishedEventRepository
import com.nadzirakarimantika.dicodingevent.data.Result
import com.nadzirakarimantika.dicodingevent.data.remote.response.ListEventsItem
import kotlinx.coroutines.launch

class FinishedViewModel(private val repository: FinishedEventRepository) : ViewModel() {

    private val _listEvents = MutableLiveData<List<ListEventsItem>>()
    val listEvents: LiveData<List<ListEventsItem>> = _listEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _showToastMessage = MutableLiveData<String?>()
    val showToastMessage: LiveData<String?> = _showToastMessage

    private val _noEventFound = MutableLiveData<Boolean>()
    val noEventFound: LiveData<Boolean> = _noEventFound

    fun findFinishedEvent() {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getEvents().observeForever { result ->
                handleResult(result)
            }
        }
    }

    fun searchFinishedEvents(query: String) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.searchEvents(query).observeForever { result ->
                handleResult(result)
            }
        }
    }

    private fun handleResult(result: Result<List<ListEventsItem>>) {
        when (result) {
            is Result.Loading -> {
                _isLoading.value = true
            }
            is Result.Success -> {
                _isLoading.value = false
                _noEventFound.value = false
                _listEvents.value = result.data
            }
            is Result.Error -> {
                _isLoading.value = false
                _noEventFound.value = true
            }
        }
    }

    fun clearToastMessage() {
        _showToastMessage.value = null
    }
}
