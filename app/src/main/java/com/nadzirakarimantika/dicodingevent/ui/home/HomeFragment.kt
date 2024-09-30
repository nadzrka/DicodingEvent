package com.nadzirakarimantika.dicodingevent.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.nadzirakarimantika.dicodingevent.data.response.ListEventsItem
import com.nadzirakarimantika.dicodingevent.databinding.FragmentHomeBinding
import com.nadzirakarimantika.dicodingevent.ui.DetailActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var eventHorizontalAdapter: EventHorizontalAdapter
    private lateinit var eventVerticalAdapter: EventVerticalAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.rvUpcoming.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        // Initialize the adapters with empty lists
        eventHorizontalAdapter = EventHorizontalAdapter(emptyList()) { event -> navigateToDetailEvent(event) }
        binding.rvUpcoming.adapter = eventHorizontalAdapter

        eventVerticalAdapter = EventVerticalAdapter(emptyList()) { event -> navigateToDetailEvent(event) }
        binding.rvEvent.adapter = eventVerticalAdapter

        setupSearchBar()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe finished events and update vertical adapter
        homeViewModel.listFinishedEvents.observe(viewLifecycleOwner) { listEvents ->
            eventVerticalAdapter.updateEvents(listEvents)
        }

        // Observe upcoming events and update horizontal adapter
        homeViewModel.listUpcomingEvents.observe(viewLifecycleOwner) { listEvents ->
            eventHorizontalAdapter.updateEvents(listEvents)
        }

        // Observe loading state to show/hide progress bar
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Fetch events from ViewModel
        homeViewModel.findFinishedEvent()
        homeViewModel.findUpcomingEvent()
    }

    private fun setupSearchBar() {
        val searchEditText: TextInputEditText = binding.searchEditText

        // Set up TextWatcher for real-time text changes
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()

                // Filter the events list based on the search query for both finished and upcoming events
                val filteredFinishedEvents = homeViewModel.listFinishedEvents.value?.filter { event ->
                    event.name?.contains(query, ignoreCase = true) == true
                } ?: emptyList()

                val filteredUpcomingEvents = homeViewModel.listUpcomingEvents.value?.filter { event ->
                    event.name?.contains(query, ignoreCase = true) == true
                } ?: emptyList()

                // Update the adapters with the filtered events
                eventVerticalAdapter.updateEvents(filteredFinishedEvents)
                eventHorizontalAdapter.updateEvents(filteredUpcomingEvents)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun navigateToDetailEvent(event: ListEventsItem) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_EVENT_ID, event.id.toString())
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }
}
