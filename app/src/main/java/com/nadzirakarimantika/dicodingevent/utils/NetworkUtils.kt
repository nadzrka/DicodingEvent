package com.nadzirakarimantika.dicodingevent.utils

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import com.nadzirakarimantika.dicodingevent.R

@Suppress("DEPRECATION")
object NetworkUtils {
    fun isConnectedToInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    fun showNoInternetToast(context: Context) {
        Toast.makeText(context, context.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
    }
}
