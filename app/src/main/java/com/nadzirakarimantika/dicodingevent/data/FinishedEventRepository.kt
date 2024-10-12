package com.nadzirakarimantika.dicodingevent.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.nadzirakarimantika.dicodingevent.data.local.entity.EventEntity
import com.nadzirakarimantika.dicodingevent.data.local.room.EventDao
import com.nadzirakarimantika.dicodingevent.data.remote.response.EventResponse
import com.nadzirakarimantika.dicodingevent.data.remote.response.ListEventsItem
import com.nadzirakarimantika.dicodingevent.data.remote.retrofit.ApiService
import com.nadzirakarimantika.dicodingevent.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FinishedEventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao,
    private val appExecutors: AppExecutors
) {
    private val result = MediatorLiveData<Result<List<ListEventsItem>>>()

    fun getEvents(): LiveData<Result<List<ListEventsItem>>> {
        result.value = Result.Loading
        val client = apiService.getFinishedEvent()
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents
                    val eventList = ArrayList<EventEntity>()

                    appExecutors.diskIO.execute {
                        events?.forEach { anEvent ->
                            val isBookmarked = eventDao.isEventBookmarked(anEvent.name)
                            anEvent.link?.let { link ->
                                val eventEntity = EventEntity(
                                    name = anEvent.name,
                                    beginTime = anEvent.beginTime,
                                    imageLogo = anEvent.imageLogo,
                                    summary = anEvent.summary,
                                    mediaCover = anEvent.mediaCover,
                                    registrants = anEvent.registrants,
                                    link = link,
                                    description = anEvent.description,
                                    ownerName = anEvent.ownerName,
                                    cityName = anEvent.cityName,
                                    quota = anEvent.quota,
                                    id = anEvent.id,
                                    endTime = anEvent.endTime,
                                    category = anEvent.category,
                                    isBookmarked = isBookmarked
                                )
                                eventList.add(eventEntity)  // Add only non-null eventEntity
                            }
                        }
                        eventDao.deleteAll()
                        eventDao.insertEvent(eventList)

                        // Post success result after inserting events
                        result.postValue(Result.Success(eventList.map { it.toListEventsItem() }))
                    }
                } else {
                    result.postValue(Result.Error("Error fetching events"))
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                result.postValue(Result.Error(t.message.toString()))
            }
        })

        return result
    }

    fun searchEvents(query: String): LiveData<Result<List<ListEventsItem>>> {
        val resultLiveData = MediatorLiveData<Result<List<ListEventsItem>>>()
        resultLiveData.value = Result.Loading
        val searchResults = eventDao.searchEvents("%$query%")

        searchResults.observeForever { events ->
            if (events.isNotEmpty()) {
                resultLiveData.postValue(Result.Success(events.map { it.toListEventsItem() }))
            } else {
                resultLiveData.postValue(Result.Error("No events found"))
            }
        }

        return resultLiveData
    }

    private fun EventEntity.toListEventsItem(): ListEventsItem {
        return ListEventsItem(
            name = this.name,
            beginTime = this.beginTime,
            imageLogo = this.imageLogo,
            summary = this.summary,
            mediaCover = this.mediaCover,
            registrants = this.registrants,
            link = this.link,
            description = this.description,
            ownerName = this.ownerName,
            cityName = this.cityName,
            quota = this.quota,
            id = this.id,
            endTime = this.endTime,
            category = this.category,
        )
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
