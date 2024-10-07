@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nadzirakarimantika.dicodingevent.data.response.EventResponse
import com.nadzirakarimantika.dicodingevent.data.response.ListEventsItem
import com.nadzirakarimantika.dicodingevent.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _listFinishedEvents = MutableLiveData<List<ListEventsItem>>()
    val listFinishedEvents: LiveData<List<ListEventsItem>> get() = _listFinishedEvents

    private val _listUpcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val listUpcomingEvents: LiveData<List<ListEventsItem>> get() = _listUpcomingEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _showToastMessage = MutableLiveData<String?>()
    val showToastMessage: LiveData<String?> get() = _showToastMessage

    private val tag = "HomeViewModel"

    private fun fetchEvents(apiCall: Call<EventResponse>, eventList: MutableLiveData<List<ListEventsItem>>) {
        _isLoading.value = true
        apiCall.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    eventList.value = responseBody?.listEvents?.filterNotNull() ?: emptyList()
                    if (eventList.value.isNullOrEmpty()) {
                        _showToastMessage.value = "No events found"
                    }
                } else {
                    Log.e(tag, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _showToastMessage.value = "Failed to load events. Please try again."
                Log.e(tag, "onFailure: ${t.message}")
            }

        })
    }

    fun findFinishedEvent() {
        fetchEvents(ApiConfig.getApiService().getFinishedEvent(), _listFinishedEvents)
    }

    fun findUpcomingEvent() {
        fetchEvents(ApiConfig.getApiService().getUpcomingEvent(), _listUpcomingEvents)
    }

    fun searchUpcomingEvents(query: String) {
        fetchEvents(ApiConfig.getApiService().searchUpcomingEvents(query), _listUpcomingEvents)
    }

    fun searchFinishedEvents(query: String) {
        fetchEvents(ApiConfig.getApiService().searchFinishedEvents(query), _listFinishedEvents)
    }


    fun clearToastMessage() {
        _showToastMessage.value = null
    }
}