package com.nadzirakarimantika.dicodingevent.di

import android.content.Context
import com.nadzirakarimantika.dicodingevent.data.FinishedEventRepository
import com.nadzirakarimantika.dicodingevent.data.local.room.EventDatabase
import com.nadzirakarimantika.dicodingevent.data.retrofit.ApiConfig
import com.nadzirakarimantika.dicodingevent.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): FinishedEventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()
        val appExecutors = AppExecutors()
        return FinishedEventRepository.getInstance(apiService, dao, appExecutors)
    }
}