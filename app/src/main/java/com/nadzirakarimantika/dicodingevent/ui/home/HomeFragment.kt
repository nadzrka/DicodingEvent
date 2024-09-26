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
import com.nadzirakarimantika.dicodingevent.ui.finished.FinishedViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel:HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get references to SearchBar and SearchView
        val searchBar: SearchBar = binding.searchBar
        val searchView: SearchView = binding.searchView

        // Handle when SearchBar is clicked to open SearchView
        searchBar.setOnMenuItemClickListener {
            searchView.show() // Show the SearchView
            true
        }

        // Set up a listener for when the search query is submitted
        searchView.editText.setOnEditorActionListener { v, actionId, event ->
            val query = searchView.text.toString()
            if (query.isNotBlank()) {
                performSearch(query)
            }
            true
        }

        searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.HIDDEN) {
                searchView.hide() // Hide the SearchView when closed
            }
        }

        homeViewModel.listEvents.observe(viewLifecycleOwner, Observer { listEvents ->
            val adapter = CarouselAdapter(listEvents) { event ->
                navigateToDetailEvent(event)
            }
            binding.viewPager.adapter = adapter
        })

        // Observe loading state to show/hide progress bar
        homeViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })

        homeViewModel.findEvent()
    }

    private fun navigateToDetailEvent(event: ListEventsItem) {
        // Start DetailActivity with event.id as an extra
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_EVENT_ID, event.id.toString()) // Pass the event ID
        }
        startActivity(intent)
    }

    private fun performSearch(query: String) {
        // Perform the search action with the query text
        Toast.makeText(requireContext(), "Searching for: $query", Toast.LENGTH_SHORT).show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
