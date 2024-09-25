package com.nadzirakarimantika.dicodingevent.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.search.SearchView
import com.google.android.material.search.SearchBar
import com.nadzirakarimantika.dicodingevent.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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

        // Set up a listener for closing the SearchView
        searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.HIDDEN) {
                searchView.hide() // Hide the SearchView when closed
            }
        }
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
