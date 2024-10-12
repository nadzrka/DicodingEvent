@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.ui.finished

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nadzirakarimantika.dicodingevent.data.FinishedEventRepository
import com.nadzirakarimantika.dicodingevent.data.Result
import com.nadzirakarimantika.dicodingevent.data.remote.response.EventResponse
import com.nadzirakarimantika.dicodingevent.data.remote.response.ListEventsItem
import com.nadzirakarimantika.dicodingevent.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FinishedViewModel(private val repository: FinishedEventRepository) : ViewModel() {

    private val _listEvents = MutableLiveData<List<ListEventsItem>>()
    val listEvents: LiveData<List<ListEventsItem>> = _listEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _showToastMessage = MutableLiveData<String?>()
    val showToastMessage: LiveData<String?> = _showToastMessage

    fun findFinishedEvent() {
        _isLoading.value = true
        repository.getEvents(ApiConfig.getApiService().getFinishedEvent())
            .observeForever { result ->
                when (result) {
                    is com.nadzirakarimantika.dicodingevent.data.Result.Loading -> {
                        _isLoading.value = true
                    }
                    is Result.Success -> {
                        _isLoading.value = false
                        _listEvents.value = result.data // Assign the list of ListEventsItem
                    }
                    is Result.Error -> {
                        _isLoading.value = false
                    }
                }
            }
    }

    fun searchFinishedEvents(query: String) {
        _isLoading.value = true
        repository.getEvents(ApiConfig.getApiService().searchFinishedEvents(query))
            .observeForever { result ->
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
    }
}
