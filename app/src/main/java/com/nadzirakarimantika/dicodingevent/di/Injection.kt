@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.di

import android.content.Context
import com.nadzirakarimantika.dicodingevent.data.EventRepository
import com.nadzirakarimantika.dicodingevent.data.local.room.EventDatabase
import com.nadzirakarimantika.dicodingevent.data.remote.retrofit.ApiConfig
import com.nadzirakarimantika.dicodingevent.utils.AppExecutors

object Injection {
    fun provideEventRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()
        val appExecutors = AppExecutors()
        return EventRepository.getInstance(apiService, dao, appExecutors)
    }
}