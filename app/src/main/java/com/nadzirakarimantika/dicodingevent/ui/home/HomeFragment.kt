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
import com.nadzirakarimantika.dicodingevent.data.remote.response.ListEventsItem
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

        eventHorizontalAdapter = EventHorizontalAdapter(emptyList()) { event -> navigateToDetailEvent(event) }
        binding.rvUpcoming.adapter = eventHorizontalAdapter

        eventVerticalAdapter = EventVerticalAdapter(emptyList()) { event -> navigateToDetailEvent(event) }
        binding.rvEvent.adapter = eventVerticalAdapter

        setupSearchView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvNoEvent = binding.tvNoEvent
        val tvNoEvent2 = binding.tvNoEvent2
        val rvEvent = binding.rvEvent
        val rvUpcoming = binding.rvUpcoming

        homeViewModel.listFinishedEvents.observe(viewLifecycleOwner) { listEvents ->
            if (listEvents.isEmpty()) {
                tvNoEvent.visibility = View.VISIBLE
                tvNoEvent2.visibility = View.VISIBLE
                rvEvent.visibility = View.GONE
            } else {
                tvNoEvent.visibility = View.GONE
                rvEvent.visibility = View.VISIBLE
                eventVerticalAdapter.updateEvents(listEvents)
            }
        }

        homeViewModel.listUpcomingEvents.observe(viewLifecycleOwner) { listEvents ->
            if (listEvents.isEmpty()) {
                tvNoEvent.visibility = View.VISIBLE
                rvUpcoming.visibility = View.GONE
                tvNoEvent2.visibility = View.VISIBLE
            } else {
                tvNoEvent.visibility = View.GONE
                rvUpcoming.visibility = View.VISIBLE
                eventHorizontalAdapter.updateEvents(listEvents)
            }
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        homeViewModel.showToastMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                homeViewModel.clearToastMessage()
            }
        }

        homeViewModel.findFinishedEvent()
        homeViewModel.findUpcomingEvent()
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
                    homeViewModel.findUpcomingEvent()
                    homeViewModel.findFinishedEvent()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    homeViewModel.findFinishedEvent()
                    homeViewModel.findUpcomingEvent()
                }
                return true
            }
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
        _binding = null
    }
}
