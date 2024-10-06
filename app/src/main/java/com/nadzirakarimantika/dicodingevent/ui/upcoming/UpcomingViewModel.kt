@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.ui.upcoming

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

class UpcomingViewModel : ViewModel() {

    private val _listEvents = MutableLiveData<List<ListEventsItem>>()
    val listEvents: LiveData<List<ListEventsItem>> get() = _listEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _showToastMessage = MutableLiveData<String?>()
    val showToastMessage: LiveData<String?> get() = _showToastMessage

    private val tag = "FinishedViewModel"

    private fun fetchEvents(apiCall: Call<EventResponse>, eventList: MutableLiveData<List<ListEventsItem>>) {
        _isLoading.value = true
        apiCall.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    eventList.value = responseBody?.listEvents?.filterNotNull() ?: emptyList()
                    if (eventList.value.isNullOrEmpty()){
                        _showToastMessage.value = "No events found"
                    }
                } else {
                    Log.e(tag, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(tag, "onFailure: ${t.message}")
            }
        })
    }

    fun findUpcomingEvents() {
        fetchEvents(ApiConfig.getApiService().getUpcomingEvent(), _listEvents)
    }

    fun searchUpcomingEvents(query: String) {
        fetchEvents(ApiConfig.getApiService().searchUpcomingEvents(query), _listEvents)
    }

    fun clearToastMessage() {
        _showToastMessage.value = null
    }
}