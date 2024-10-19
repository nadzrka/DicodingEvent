package com.nadzirakarimantika.dicodingevent.ui

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.nadzirakarimantika.dicodingevent.ui.home.EventWorker
import java.util.concurrent.TimeUnit

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        schedulePeriodicEventNotification()
    }

    private fun schedulePeriodicEventNotification() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequest.Builder(EventWorker::class.java, 1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "EventNotificationWork",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }
}

