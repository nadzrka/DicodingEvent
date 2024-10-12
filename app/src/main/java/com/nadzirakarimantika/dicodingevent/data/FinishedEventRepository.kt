package com.nadzirakarimantika.dicodingevent.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.nadzirakarimantika.dicodingevent.BuildConfig
import com.nadzirakarimantika.dicodingevent.data.local.entity.EventEntity
import com.nadzirakarimantika.dicodingevent.data.local.room.EventDao
import com.nadzirakarimantika.dicodingevent.data.remote.response.EventResponse
import com.nadzirakarimantika.dicodingevent.data.remote.response.ListEventsItem
import com.nadzirakarimantika.dicodingevent.data.retrofit.ApiConfig
import com.nadzirakarimantika.dicodingevent.data.retrofit.ApiService
import com.nadzirakarimantika.dicodingevent.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FinishedEventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao,
    private val appExecutors: AppExecutors
) {
    private val result = MediatorLiveData<Result<List<ListEventsItem>>>() // Change the result type

    fun getEvents(apiCall: Call<EventResponse>): LiveData<Result<List<ListEventsItem>>> {
        result.value = Result.Loading
        val client = apiService.getFinishedEvent()
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents?.filterNotNull() // Filter non-null events
                    result.postValue(Result.Success(events ?: emptyList())) // Return list of ListEventsItem
                } else {
                    result.postValue(Result.Error("Error fetching data"))
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                result.postValue(Result.Error(t.message.toString()))
            }
        })

        return result
    }

    companion object {
        @Volatile
        private var instance: FinishedEventRepository? = null
        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao,
            appExecutors: AppExecutors
        ): FinishedEventRepository =
            instance ?: synchronized(this) {
                instance ?: FinishedEventRepository(apiService, eventDao, appExecutors)
            }.also { instance = it }
    }
}
