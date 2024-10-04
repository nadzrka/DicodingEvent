@file:Suppress("unused", "RedundantSuppression")
package com.nadzirakarimantika.dicodingevent.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nadzirakarimantika.dicodingevent.data.response.DetailResponse
import com.nadzirakarimantika.dicodingevent.data.response.Event
import com.nadzirakarimantika.dicodingevent.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {
    private val _detailEvent = MutableLiveData<Event>()
    val event: LiveData<Event> get() = _detailEvent

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val tag = "DetailViewModel"

    fun findEvent(eventId: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailEvent(eventId)
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val event = responseBody?.event ?: throw IllegalStateException("Event is null but expected a non-nullable value")
                    _detailEvent.value = event
                } else {
                    Log.e(tag, "Response not successful: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(tag, "onFailure: ${t.message}")
            }
        })
    }
}