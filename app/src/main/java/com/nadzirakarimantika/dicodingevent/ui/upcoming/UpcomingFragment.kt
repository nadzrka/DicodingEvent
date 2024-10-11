@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.ui.upcoming

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
        val tvNoEvent = binding.tvNoEvent
        val rvUpcoming = binding.rvEvent

        eventAdapter = EventAdapter(mutableListOf()) { event ->
            navigateToDetailEvent(event)
        }

        binding.rvEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvent.adapter = eventAdapter

        setupSearchView()

        upcomingViewModel.listEvents.observe(viewLifecycleOwner) { listEvents ->
            if (listEvents.isEmpty()) {
                tvNoEvent.visibility = View.VISIBLE
                rvUpcoming.visibility = View.GONE
            } else {
                tvNoEvent.visibility = View.GONE
                rvUpcoming.visibility = View.VISIBLE
                eventAdapter.updateEvents(listEvents)
            }
        }

        upcomingViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        upcomingViewModel.showToastMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                upcomingViewModel.clearToastMessage()
            }
        }

        upcomingViewModel.findUpcomingEvents()
    }

    private fun setupSearchView() {
        val searchView = binding.searchView

        searchView.visibility = View.VISIBLE
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    upcomingViewModel.searchUpcomingEvents(query)
                } else {
                    upcomingViewModel.findUpcomingEvents()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                   upcomingViewModel.findUpcomingEvents()
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
