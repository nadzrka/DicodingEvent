@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.ui.finished

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nadzirakarimantika.dicodingevent.data.FinishedEventRepository
import com.nadzirakarimantika.dicodingevent.data.remote.response.ListEventsItem
import com.nadzirakarimantika.dicodingevent.databinding.FragmentFinishedBinding
import com.nadzirakarimantika.dicodingevent.ui.DetailActivity
import com.nadzirakarimantika.dicodingevent.ui.EventAdapter
import com.nadzirakarimantika.dicodingevent.ui.ViewModelFactory

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventAdapter: EventAdapter
    private lateinit var finishedViewModel: FinishedViewModel

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
        val tvNoEvent = binding.tvNoEvent

        eventAdapter = EventAdapter(mutableListOf()) { event ->
            navigateToDetailEvent(event)
        }

        binding.rvEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvent.adapter = eventAdapter

        setupSearchView()

        // Set up ViewModel using ViewModelFactory
        val factory = ViewModelFactory.getInstance(requireActivity())
        finishedViewModel = viewModels<FinishedViewModel> { factory }.value

        // Observe live data for the event list
        finishedViewModel.listEvents.observe(viewLifecycleOwner) { listEvents ->
            if (listEvents.isEmpty()) {
                tvNoEvent.visibility = View.VISIBLE
                binding.rvEvent.visibility = View.GONE
            } else {
                tvNoEvent.visibility = View.GONE
                binding.rvEvent.visibility = View.VISIBLE
                eventAdapter.updateEvents(listEvents)
            }
        }

        // Observe loading state
        finishedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe toast messages
        finishedViewModel.showToastMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch events
        finishedViewModel.findFinishedEvent()
    }

    private fun setupSearchView() {
        val searchView = binding.searchView
        searchView.visibility = View.VISIBLE

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    finishedViewModel.searchFinishedEvents(query)
                } else {
                    finishedViewModel.findFinishedEvent()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    finishedViewModel.findFinishedEvent()
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
