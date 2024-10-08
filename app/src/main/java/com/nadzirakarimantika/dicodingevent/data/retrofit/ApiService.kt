package com.nadzirakarimantika.dicodingevent.data.retrofit

import com.nadzirakarimantika.dicodingevent.data.response.DetailResponse
import com.nadzirakarimantika.dicodingevent.data.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("https://event-api.dicoding.dev/events?active=1")  // Update with the correct endpoint to get all events
    fun getUpcomingEvent(): Call<EventResponse>

    @GET("https://event-api.dicoding.dev/events?active=0")  // Update with the correct endpoint to get all events
    fun getFinishedEvent(): Call<EventResponse>

    @GET("https://event-api.dicoding.dev/events/{id}")  // Update with the correct endpoint to get all events
    fun getDetailEvent(@Path("id") eventId: String): Call<DetailResponse>
}