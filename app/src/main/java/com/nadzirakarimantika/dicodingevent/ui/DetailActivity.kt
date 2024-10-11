@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import androidx.core.text.HtmlCompat
import com.nadzirakarimantika.dicodingevent.R
import com.nadzirakarimantika.dicodingevent.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(ContextCompat.getDrawable(this@DetailActivity, R.drawable.arrow_back_24dp_e8eaed_fill0_wght400_grad0_opsz24)) // Set custom back button
            title = getString(R.string.detail_event)
        }

        if (!isConnectedToInternet()) {
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
        }

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID)

        if (eventId != null) {
            detailViewModel.findEvent(eventId)
        } else {
            Log.e("DetailActivity", "Event ID is null")
        }

        detailViewModel.event.observe(this) { event ->
            if (event != null) {
                val remainingQuota = (event.quota ?: 0) - (event.registrants ?: 0)
                binding.eventName.text =  HtmlCompat.fromHtml(
                    event.name.toString(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                binding.eventDescription.text =  HtmlCompat.fromHtml(
                    event.description.toString(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                binding.eventCategory.text =  HtmlCompat.fromHtml(
                    event.category.toString(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                binding.eventOwner.text = HtmlCompat.fromHtml(
                    "Diselenggarakan oleh: " + event.ownerName.toString(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                binding.eventCity.text =  HtmlCompat.fromHtml(
                    event.cityName.toString(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                binding.eventSummary.text =  HtmlCompat.fromHtml(
                    event.summary.toString(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                binding.eventQuota.text =  HtmlCompat.fromHtml(
                    "Sisa quota: $remainingQuota",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                binding.eventBeginTime.text =  HtmlCompat.fromHtml(
                    event.beginTime.toString(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )

                binding.linkButton.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(event.link.toString())
                    startActivity(intent)
                }

                Glide.with(this)
                    .load(event.mediaCover)
                    .into(binding.imageView)
            } else {
                Log.e("DetailActivity", "Event data is null")
            }
        }

        detailViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

    }

    private fun isConnectedToInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
    }

}
