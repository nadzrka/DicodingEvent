package com.nadzirakarimantika.dicodingevent.ui.finished

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.nadzirakarimantika.dicodingevent.R
import com.nadzirakarimantika.dicodingevent.data.response.ListEventsItem
import com.nadzirakarimantika.dicodingevent.databinding.FragmentFinishedBinding
import com.nadzirakarimantika.dicodingevent.ui.DetailActivity
import com.nadzirakarimantika.dicodingevent.ui.EventAdapter

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private val finishedViewModel: FinishedViewModel by viewModels()

    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the adapter with an empty list initially
        eventAdapter = EventAdapter(emptyList()) { event ->
            navigateToDetailEvent(event) // Handle click event for each item
        }

        // Set the adapter to the RecyclerView
        binding.rvEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvent.adapter = eventAdapter

        setupSearchBar()

        // Observe listEvents from ViewModel and update the adapter when data is available
        finishedViewModel.listEvents.observe(viewLifecycleOwner, Observer { listEvents ->
            eventAdapter.updateEvents(listEvents) // Update the adapter with new data
        })

        // Observe loading state to show/hide progress bar
        finishedViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        // Fetch events from ViewModel
        finishedViewModel.findEvent()
    }

    private fun setupSearchBar() {
        val searchEditText: TextInputEditText = binding.searchBar.findViewById(R.id.searchEditText)

        // Listen for text changes and filter the list
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                filterEvents(query)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun filterEvents(query: String) {
        val originalList = finishedViewModel.listEvents.value ?: emptyList()
        val filteredList = originalList.filter { event ->
            event.name?.contains(query, ignoreCase = true) == true
        }
        eventAdapter.updateEvents(filteredList)
    }

    private fun navigateToDetailEvent(event: ListEventsItem) {
        // Start DetailActivity with event.id as an extra
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_EVENT_ID, event.id.toString()) // Pass the event ID
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
