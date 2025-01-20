@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.ui

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import androidx.core.text.HtmlCompat
import com.bumptech.glide.request.RequestOptions
import com.nadzirakarimantika.dicodingevent.R
import com.nadzirakarimantika.dicodingevent.data.Result
import com.nadzirakarimantika.dicodingevent.data.local.entity.EventEntity
import com.nadzirakarimantika.dicodingevent.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private var isBookmarked = false
    private var currentEvent: EventEntity? = null
    private val detailViewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isConnectedToInternet()) {
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
        }

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, R.color.white))

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(
                ContextCompat.getDrawable(
                    this@DetailActivity,
                    R.drawable.arrow_back_24dp_e8eaed_fill0_wght400_grad0_opsz24
                )
            )
            title = getString(R.string.detail_event)
        }

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID)

        if (eventId != null) {
            observeEventDetails(eventId)
        }

        binding.floatingActionButton.setOnClickListener {
            toggleBookmark()
        }
    }

    private fun observeEventDetails(eventId: String) {
        detailViewModel.getDetailEvent(eventId).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val event = result.data
                    currentEvent = event
                    isBookmarked = event.isBookmarked
                    updateFabIcon(isBookmarked)
                    populateEventDetails(event)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun populateEventDetails(event: EventEntity) {
        val remainingQuota = event.quota - event.registrants
        binding.apply {
            eventName.text = HtmlCompat.fromHtml(event.name, HtmlCompat.FROM_HTML_MODE_LEGACY)
            eventDescription.text = HtmlCompat.fromHtml(event.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
            eventCategory.text = HtmlCompat.fromHtml(event.category, HtmlCompat.FROM_HTML_MODE_LEGACY)
            eventOwner.text = HtmlCompat.fromHtml(getString(R.string.diselenggarakan_oleh, event.ownerName), HtmlCompat.FROM_HTML_MODE_LEGACY)
            eventCity.text = HtmlCompat.fromHtml(event.cityName, HtmlCompat.FROM_HTML_MODE_LEGACY)
            eventSummary.text = HtmlCompat.fromHtml(event.summary, HtmlCompat.FROM_HTML_MODE_LEGACY)
            eventQuota.text = HtmlCompat.fromHtml(getString(R.string.sisa_quota, remainingQuota.toString()), HtmlCompat.FROM_HTML_MODE_LEGACY)
            eventBeginTime.text = HtmlCompat.fromHtml(event.beginTime, HtmlCompat.FROM_HTML_MODE_LEGACY)

            linkButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(event.link)
                startActivity(intent)
            }
        }

        Glide.with(this)
            .load(event.mediaCover)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fitCenter()
            )
            .into(binding.imageView)
    }

    private fun toggleBookmark() {
        isBookmarked = !isBookmarked
        currentEvent?.let { event ->
            updateFabIcon(isBookmarked)
            if (isBookmarked) {
                detailViewModel.saveEvent(event)
                Toast.makeText(this, getString(R.string.added_to_favorite), Toast.LENGTH_SHORT).show()
            } else {
                detailViewModel.deleteEvent(event)
                Toast.makeText(this, getString(R.string.removed_from_favorite), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFabIcon(isBookmarked: Boolean) {
        val iconRes = if (isBookmarked) R.drawable.favorite else R.drawable.baseline_favorite_border_24
        binding.floatingActionButton.setImageResource(iconRes)
    }

    private fun isConnectedToInternet(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
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
