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
import com.nadzirakarimantika.dicodingevent.data.remote.response.ListEventsItem
import com.nadzirakarimantika.dicodingevent.databinding.FragmentHomeBinding
import com.nadzirakarimantika.dicodingevent.ui.DetailActivity
import com.nadzirakarimantika.dicodingevent.ui.ViewModelFactory
import com.nadzirakarimantika.dicodingevent.ui.finished.FinishedViewModel

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

        eventHorizontalAdapter = EventHorizontalAdapter { event -> navigateToDetailEvent(event) }
        binding.rvUpcoming.adapter = eventHorizontalAdapter

        eventVerticalAdapter = EventVerticalAdapter { event -> navigateToDetailEvent(event) }
        binding.rvEvent.adapter = eventVerticalAdapter

        setupSearchView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: HomeViewModel by viewModels {
            factory
        }

        viewModel.getFinishedEvents().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.tvNoEvent.visibility = View.GONE
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.tvNoEvent.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                        val eventData = result.data
                        eventVerticalAdapter.submitList(eventData)
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvNoEvent.visibility = View.GONE
                        Toast.makeText(
                            context,
                            result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        viewModel.getUpcomingEvents().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.tvNoEvent.visibility = View.GONE
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.tvNoEvent.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                        val eventData = result.data
                        eventHorizontalAdapter.submitList(eventData)
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvNoEvent.visibility = View.GONE
                        Toast.makeText(
                            context,
                            result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        homeViewModel.getUpcomingEvents()
        homeViewModel.getFinishedEvents()
    }

    private fun setupSearchView() {
        val searchView = binding.searchView
        binding.searchView.visibility = View.VISIBLE
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()){
                    homeViewModel.searchUpcomingEvents(query)
                    homeViewModel.searchFinishedEvents(query)
                } else {
                    homeViewModel.getUpcomingEvents()
                    homeViewModel.getFinishedEvents()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    homeViewModel.getFinishedEvents()
                    homeViewModel.getUpcomingEvents()
                }
                return true
            }
        })
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
