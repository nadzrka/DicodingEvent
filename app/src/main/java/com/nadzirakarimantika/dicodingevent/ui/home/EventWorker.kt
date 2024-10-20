package com.nadzirakarimantika.dicodingevent.ui.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.nadzirakarimantika.dicodingevent.BuildConfig
import com.nadzirakarimantika.dicodingevent.R
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class EventWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        private val TAG = EventWorker::class.java.simpleName
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "dicoding channel"
    }

    override fun doWork(): Result {
        getCurrentEvent()
        return Result.success()
    }

    private fun getCurrentEvent() {
        val client = AsyncHttpClient()
        val endpoint = "events?active=-1&limit=1"
        val url = BuildConfig.BASE_URL + endpoint
        Log.d(TAG, "getCurrentEvent: $url")

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header?>?, responseBody: ByteArray) {
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val name: String = responseObject.getJSONArray("listEvents").getJSONObject(0).getString("name")
                    val date: String = responseObject.getJSONArray("listEvents").getJSONObject(0).getString("beginTime")
                    val title = "Event yang akan datang: $name"
                    val time = "Diadakan pada: $date"
                    showNotification(title, time)
                    Log.d(TAG, "onSuccess: Completed.")
                } catch (e: Exception) {
                    showNotification("Event Retrieval Failed", e.message)
                    Log.e(TAG, "onSuccess: Failed to parse the event data.", e)
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header?>?, responseBody: ByteArray?, error: Throwable) {
                Log.e(TAG, "onFailure: Failed to retrieve the event.", error)
                showNotification("Event Retrieval Failed", error.message)
            }
        })
    }

    private fun showNotification(title: String, summary: String?) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(summary)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        notification.setChannelId(CHANNEL_ID)
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }
}
