@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nadzirakarimantika.dicodingevent.data.Result
import com.nadzirakarimantika.dicodingevent.data.local.entity.EventEntity
import com.nadzirakarimantika.dicodingevent.databinding.FragmentHomeBinding
import com.nadzirakarimantika.dicodingevent.ui.DetailActivity
import com.nadzirakarimantika.dicodingevent.ui.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels { ViewModelFactory.getInstance(requireActivity()) }
    private lateinit var eventHorizontalAdapter: EventHorizontalAdapter
    private lateinit var eventVerticalAdapter: EventVerticalAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupRecyclerViews()
        setupSearchView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeEvents()
    }

    private fun setupRecyclerViews() {
        eventHorizontalAdapter = EventHorizontalAdapter { navigateToDetailEvent(it) }
        binding.rvUpcoming.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = eventHorizontalAdapter
        }

        eventVerticalAdapter = EventVerticalAdapter { navigateToDetailEvent(it) }
        binding.rvEvent.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = eventVerticalAdapter
        }
    }

    private fun observeEvents() {
        homeViewModel.getUpcomingEvents().observe(viewLifecycleOwner) { result ->
            handleEventResult(result, eventHorizontalAdapter)
        }

        homeViewModel.getFinishedEvents().observe(viewLifecycleOwner) { result ->
            handleEventResult(result, eventVerticalAdapter)
        }
    }

    private fun handleEventResult(result: Result<List<EventEntity>>, adapter: androidx.recyclerview.widget.ListAdapter<EventEntity, *>) {
        when (result) {
            is Result.Loading -> {
                binding.tvNoEvent.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
            is Result.Success -> {
                binding.tvNoEvent.visibility = if (result.data.isEmpty()) View.VISIBLE else View.GONE
                binding.progressBar.visibility = View.GONE
                adapter.submitList(result.data)
            }
            is Result.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.tvNoEvent.visibility = View.GONE
                Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val searchQuery = query.orEmpty()
                if (searchQuery.isNotEmpty()) {
                    searchEvents(searchQuery)
                } else {
                    resetToAllEvents()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    resetToAllEvents()
                }
                return true
            }
        })
    }

    private fun resetToAllEvents() {
        homeViewModel.getUpcomingEvents().observe(viewLifecycleOwner) { result ->
            handleEventResult(result, eventHorizontalAdapter)
        }
        homeViewModel.getFinishedEvents().observe(viewLifecycleOwner) { result ->
            handleEventResult(result, eventVerticalAdapter)
        }
    }


    private fun searchEvents(query: String) {
        homeViewModel.searchUpcomingEvents(query).observe(viewLifecycleOwner) { result ->
            handleSearchResult(result, eventHorizontalAdapter)
        }
        homeViewModel.searchFinishedEvents(query).observe(viewLifecycleOwner) { result ->
            handleSearchResult(result, eventVerticalAdapter)
        }
    }

    private fun handleSearchResult(result: Result<List<EventEntity>>, adapter: androidx.recyclerview.widget.ListAdapter<EventEntity, *>) {
        when (result) {
            is Result.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvNoEvent.visibility = View.GONE
            }
            is Result.Success -> {
                binding.progressBar.visibility = View.GONE
                if (result.data.isEmpty()) {
                    binding.tvNoEvent.visibility = View.VISIBLE
                    adapter.submitList(emptyList())
                } else {
                    binding.tvNoEvent.visibility = View.GONE
                    adapter.submitList(result.data)
                }
            }
            is Result.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.tvNoEvent.visibility = View.VISIBLE
                binding.tvNoEvent2.visibility = View.VISIBLE
                adapter.submitList(emptyList())
                Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToDetailEvent(event: EventEntity) {
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
