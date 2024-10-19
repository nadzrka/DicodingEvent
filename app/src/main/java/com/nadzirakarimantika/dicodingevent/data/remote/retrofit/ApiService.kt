@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.data.remote.retrofit

import com.nadzirakarimantika.dicodingevent.data.remote.response.DetailResponse
import com.nadzirakarimantika.dicodingevent.data.remote.response.EventResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("https://event-api.dicoding.dev/events?active=1")
    suspend fun getUpcomingEvent(): EventResponse

    @GET("https://event-api.dicoding.dev/events?active=0")
    suspend fun getFinishedEvent(): EventResponse

    @GET("https://event-api.dicoding.dev/events")
    suspend fun getEvent()

    @GET("https://event-api.dicoding.dev/events/{id}")
    suspend fun getDetailEvent(@Path("id") eventId: String): DetailResponse

    @GET("events")
    suspend fun searchFinishedEvents(
        @Query("q") query: String,
        @Query("active") active: Int = 0
    )

    @GET("events")
    suspend fun searchUpcomingEvents(
        @Query("q") query: String,
        @Query("active") active: Int = 1
    )
}