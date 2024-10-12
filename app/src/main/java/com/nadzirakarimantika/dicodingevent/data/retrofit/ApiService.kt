package com.nadzirakarimantika.dicodingevent.data.retrofit

import com.nadzirakarimantika.dicodingevent.data.remote.response.DetailResponse
import com.nadzirakarimantika.dicodingevent.data.remote.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("https://event-api.dicoding.dev/events?active=1")
    fun getUpcomingEvent(): Call<EventResponse>

    @GET("https://event-api.dicoding.dev/events?active=0")
    fun getFinishedEvent(): Call<EventResponse>

    @GET("https://event-api.dicoding.dev/events/{id}")
    fun getDetailEvent(@Path("id") eventId: String): Call<DetailResponse>

    @GET("events")
    fun searchFinishedEvents(
        @Query("q") query: String,
        @Query("active") active: Int = 0
    ): Call<EventResponse>

    @GET("events")
    fun searchUpcomingEvents(
        @Query("q") query: String,
        @Query("active") active: Int = 1
    ): Call<EventResponse>
}