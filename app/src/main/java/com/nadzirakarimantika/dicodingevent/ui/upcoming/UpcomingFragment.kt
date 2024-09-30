package com.nadzirakarimantika.dicodingevent.ui.upcoming

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nadzirakarimantika.dicodingevent.data.response.ListEventsItem
import com.nadzirakarimantika.dicodingevent.databinding.FragmentUpcomingBinding
import com.nadzirakarimantika.dicodingevent.ui.DetailActivity
import com.nadzirakarimantika.dicodingevent.ui.EventAdapter

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private val upcomingViewModel: UpcomingViewModel by viewModels()

    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize adapter with an empty list and attach it to the RecyclerView
        eventAdapter = EventAdapter(emptyList()) { event ->
            navigateToDetailEvent(event)
        }
        binding.rvEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvent.adapter = eventAdapter

        // Observe the list of events and update the adapter when data changes
        upcomingViewModel.listEvents.observe(viewLifecycleOwner, Observer { listEvents ->
            eventAdapter.updateEvents(listEvents)
        })

        // Observe loading state to show/hide progress bar
        upcomingViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        // Fetch events from the ViewModel
        upcomingViewModel.findEvent()
    }

    private fun navigateToDetailEvent(event: ListEventsItem) {
        // Start DetailActivity with the event ID as an extra
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_EVENT_ID, event.id.toString())
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
