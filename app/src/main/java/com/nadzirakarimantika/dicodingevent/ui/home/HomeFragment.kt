package com.nadzirakarimantika.dicodingevent.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.search.SearchView
import com.google.android.material.search.SearchBar
import com.nadzirakarimantika.dicodingevent.data.response.ListEventsItem
import com.nadzirakarimantika.dicodingevent.databinding.FragmentHomeBinding
import com.nadzirakarimantika.dicodingevent.ui.DetailActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchBar: SearchBar = binding.searchBar
        val searchView: SearchView = binding.searchView

        searchBar.setOnMenuItemClickListener {
            searchView.show()
            true
        }

        searchView.editText.setOnEditorActionListener { _, _, _ ->
            val query = searchView.text.toString()
            if (query.isNotBlank()) {
                performSearch(query)
            }
            true
        }

        searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.HIDDEN) {
                searchView.hide()
            }
        }

        // Set layout managers
        binding.rvEvent.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvUpcoming.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Observe list of events
        homeViewModel.listFinishedEvents.observe(viewLifecycleOwner, Observer { listEvents ->
            // Create separate adapter for horizontal RecyclerView
            val eventVerticalAdapter = EventVerticalAdapter(listEvents) { event ->
                navigateToDetailEvent(event)
            }
            binding.rvEvent.adapter = eventVerticalAdapter
        })

        // Observe upcoming events
        homeViewModel.listUpcomingEvents.observe(viewLifecycleOwner, Observer { upcomingEvents ->
            // Create separate adapter for vertical RecyclerView
            val eventHorizontalAdapter = EventHorizontalAdapter(upcomingEvents) { event ->
                navigateToDetailEvent(event)
            }
            binding.rvUpcoming.adapter = eventHorizontalAdapter
        })

        // Observe loading state to show/hide progress bar
        homeViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        // Trigger the view model to fetch the events
        homeViewModel.findFinishedEvent()
        homeViewModel.findUpcomingEvent()
    }

    private fun navigateToDetailEvent(event: ListEventsItem) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_EVENT_ID, event.id.toString())
        }
        startActivity(intent)
    }

    private fun performSearch(query: String) {
        Toast.makeText(requireContext(), "Searching for: $query", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
