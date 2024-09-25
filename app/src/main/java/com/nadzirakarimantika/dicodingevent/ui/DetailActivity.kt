package com.nadzirakarimantika.dicodingevent.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nadzirakarimantika.dicodingevent.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
    }

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the event ID from the Intent
        val eventId = intent.getStringExtra(EXTRA_EVENT_ID)

        // Set the title for the ActionBar
        supportActionBar?.title = "Detail Event"

        // Observe the event details and update UI
        detailViewModel.detailEvent.observe(this) { event ->
            // Make sure event is not null before accessing its properties
            if (event != null) {
                binding.eventName.text = event.name
                binding.eventDescription.text = event.description
            }
        }

        // Observe the loading state and show/hide the progress bar
        detailViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        // Fetch the event details
        if (eventId != null) {
            detailViewModel.findEvent(eventId)  // Pass eventId to the ViewModel
        }
    }
}