@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.nadzirakarimantika.dicodingevent.data.local.entity.EventEntity
import com.nadzirakarimantika.dicodingevent.data.local.room.EventDao
import com.nadzirakarimantika.dicodingevent.data.remote.response.DetailResponse
import com.nadzirakarimantika.dicodingevent.data.remote.response.Event
import com.nadzirakarimantika.dicodingevent.data.remote.response.EventResponse
import com.nadzirakarimantika.dicodingevent.data.remote.retrofit.ApiConfig
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
    private val _showToastMessage = MutableLiveData<String>()
    val showToastMessage: LiveData<String> get() = _showToastMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _detailEvent = MutableLiveData<Result<Event>>()

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

    fun searchEvents(query: String, isFinished: Boolean): LiveData<Result<List<EventEntity>>> {
        val result = MediatorLiveData<Result<List<EventEntity>>>()
        result.value = Result.Loading

        val searchResults =
            if (query.isEmpty()) {
                if (isFinished) eventDao.getFinishedEvent() else eventDao.getUpcomingEvent()
            }
            else {
                if (isFinished) eventDao.searchFinishedEvents("%$query%") else eventDao.searchUpcomingEvents("%$query%")
            }

        result.addSource(searchResults) { events ->
            result.value = if (events.isNotEmpty()) Result.Success(events) else Result.Error("No events found")
        }

        return result
    }

    private fun mapEventEntityToEvent(entity: EventEntity): Event {
        return Event(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            category = entity.category,
            ownerName = entity.ownerName,
            cityName = entity.cityName,
            summary = entity.summary,
            quota = entity.quota,
            registrants = entity.registrants,
            beginTime = entity.beginTime,
            mediaCover = entity.mediaCover,
            imageLogo = entity.imageLogo,
            endTime = entity.endTime,
            link = entity.link
        )
    }

    fun getDetailEvent(eventId : String): LiveData<Result<EventEntity>> {
        val result = MediatorLiveData<Result<EventEntity>>()
        result.value = Result.Loading
        val client = ApiConfig.getApiService().getDetailEvent(eventId)

        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(call: Call<DetailResponse>, response: Response<DetailResponse>) {
                if (response.isSuccessful) {
                    val eventDetail = response.body()?.event
                    appExecutors.diskIO.execute {
                        eventDetail?.let { event ->
                            val isBookmarked = eventDao.isEventBookmarked(event.name)
                            val eventEntity = EventEntity(
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
                            eventDao.deleteUpcomingEvents()
                            eventDao.insertEvent(listOf(eventEntity))
                        }
                    }
                } else {
                    result.postValue(Result.Error("Failed to fetch upcoming events: ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                result.postValue(Result.Error(t.message.toString()))
            }
        })

        val localData = eventDao.getEventById(eventId)
        result.addSource(localData) { eventEntity ->
            result.value = Result.Success(eventEntity)
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