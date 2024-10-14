@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.nadzirakarimantika.dicodingevent.data.local.entity.EventEntity
import com.nadzirakarimantika.dicodingevent.data.local.room.EventDao
import com.nadzirakarimantika.dicodingevent.data.remote.response.EventResponse
import com.nadzirakarimantika.dicodingevent.data.remote.retrofit.ApiService
import com.nadzirakarimantika.dicodingevent.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao,
    private val appExecutors: AppExecutors
) {

    fun getFinishedEvents(): LiveData<Result<List<EventEntity>>> {
        val result = MediatorLiveData<Result<List<EventEntity>>>()
        result.value = Result.Loading
        val client = apiService.getFinishedEvent()

        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    val listEvents = response.body()?.listEvents
                    val eventList = ArrayList<EventEntity>()
                    appExecutors.diskIO.execute {
                        listEvents?.forEach { event ->
                            val isBookmarked = eventDao.isEventBookmarked(event.name)
                            val events = EventEntity(
                                event.name,
                                event.beginTime,
                                event.imageLogo,
                                event.summary,
                                event.ownerName,
                                event.mediaCover,
                                event.registrants,
                                event.link,
                                event.description,
                                event.cityName,
                                event.quota,
                                event.id,
                                event.endTime,
                                event.category,
                                isBookmarked,
                                isActive = false
                            )
                            eventList.add(events)
                        }

                        eventDao.deleteFinishedEvents()
                        eventDao.insertEvent(eventList)
                    }
                } else {
                    result.postValue(Result.Error("Failed to fetch finished events: ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                result.postValue(Result.Error(t.message.toString()))
            }
        })

        val localData = eventDao.getFinishedEvent()
        result.addSource(localData) { newData: List<EventEntity> ->
            result.value = Result.Success(newData)
        }
        return result
    }

    fun getUpcomingEvents(): LiveData<Result<List<EventEntity>>> {
        val result = MediatorLiveData<Result<List<EventEntity>>>()
        result.value = Result.Loading
        val client = apiService.getUpcomingEvent()

        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    val listEvents = response.body()?.listEvents
                    val eventList = ArrayList<EventEntity>()
                    appExecutors.diskIO.execute {
                        listEvents?.forEach { event ->
                            val isBookmarked = eventDao.isEventBookmarked(event.name)
                            val events = EventEntity(
                                event.name,
                                event.beginTime,
                                event.imageLogo,
                                event.summary,
                                event.ownerName,
                                event.mediaCover,
                                event.registrants,
                                event.link,
                                event.description,
                                event.cityName,
                                event.quota,
                                event.id,
                                event.endTime,
                                event.category,
                                isBookmarked,
                                isActive = true
                            )
                            eventList.add(events)
                        }

                        eventDao.deleteUpcomingEvents()
                        eventDao.insertEvent(eventList)
                    }
                } else {
                    result.postValue(Result.Error("Failed to fetch upcoming events: ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                result.postValue(Result.Error(t.message.toString()))
            }
        })

        val localData = eventDao.getUpcomingEvent()
        result.addSource(localData) { newData: List<EventEntity> ->
            result.value = Result.Success(newData)
        }
        return result
    }

    fun searchFinishedEvents(query: String): LiveData<Result<List<EventEntity>>> {
        val result = MediatorLiveData<Result<List<EventEntity>>>()
        result.value = Result.Loading

        val searchResults = if (query.isEmpty()) {
            eventDao.getFinishedEvent()
        } else {
            eventDao.searchEvents("%$query%")
        }

        result.addSource(searchResults) { events ->
            if (events.isNotEmpty()) {
                result.value = Result.Success(events)
            } else {
                result.value = Result.Error("No events found")
            }
        }

        return result
    }

    fun searchUpcomingEvents(query: String): LiveData<Result<List<EventEntity>>> {
        val result = MediatorLiveData<Result<List<EventEntity>>>()
        result.value = Result.Loading

        val searchResults = if (query.isEmpty()) {
            eventDao.getUpcomingEvent()
        } else {
            eventDao.searchEvents("%$query%")
        }

        result.addSource(searchResults) { events ->
            if (events.isNotEmpty()) {
                result.value = Result.Success(events)
            } else {
                result.value = Result.Error("No events found")
            }
        }

        return result
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao,
            appExecutors: AppExecutors
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventDao, appExecutors)
            }.also { instance = it }
    }
}
