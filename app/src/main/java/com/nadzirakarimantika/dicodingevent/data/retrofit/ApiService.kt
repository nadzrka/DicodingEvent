package com.nadzirakarimantika.dicodingevent.data.retrofit

import com.nadzirakarimantika.dicodingevent.data.response.UpcomingResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("https://event-api.dicoding.dev/events?active=1")  // Update with the correct endpoint to get all events
    fun getEvent(): Call<UpcomingResponse>
}