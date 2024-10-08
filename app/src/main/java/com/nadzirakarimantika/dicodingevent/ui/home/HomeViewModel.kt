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

    private val _events = MutableLiveData<List<ListEventsItem>>()
    val data: LiveData<List<ListEventsItem>> get() = _events

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val tag = "FinishedViewModel"

    fun findFinishedEvent() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getFinishedEvent()
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(
                call: Call<EventResponse>,
                response: Response<EventResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _listFinishedEvents.value = responseBody.listEvents?.filterNotNull() ?: emptyList()
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

    fun findUpcomingEvent() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUpcomingEvent()
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(
                call: Call<EventResponse>,
                response: Response<EventResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _listUpcomingEvents.value = responseBody.listEvents?.filterNotNull() ?: emptyList()
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
}