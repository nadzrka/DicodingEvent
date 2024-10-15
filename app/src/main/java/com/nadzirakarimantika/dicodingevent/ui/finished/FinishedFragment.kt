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
import com.nadzirakarimantika.dicodingevent.data.Result
import com.nadzirakarimantika.dicodingevent.data.local.entity.EventEntity
import com.nadzirakarimantika.dicodingevent.databinding.FragmentFinishedBinding
import com.nadzirakarimantika.dicodingevent.ui.DetailActivity
import com.nadzirakarimantika.dicodingevent.ui.EventAdapter
import com.nadzirakarimantika.dicodingevent.ui.ViewModelFactory

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventAdapter: EventAdapter
    private val finishedViewModel: FinishedViewModel by viewModels { ViewModelFactory.getInstance(requireActivity()) }

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

        eventAdapter = EventAdapter { event ->
            navigateToDetailEvent(event)
        }

        binding.rvEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvent.adapter = eventAdapter

        setupSearchView()
        observeFinishedEvents()
    }

    private fun observeFinishedEvents() {
        finishedViewModel.findFinishedEvent().observe(viewLifecycleOwner) { result ->
            handleEventResult(result, eventAdapter)
        }
    }

    private fun handleEventResult(result: Result<List<EventEntity>>, adapter: EventAdapter) {
        when (result) {
            is Result.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvNoEvent.visibility = View.GONE
            }
            is Result.Success -> {
                binding.progressBar.visibility = View.GONE
                val eventData = result.data
                if (eventData.isEmpty()) {
                    binding.tvNoEvent.visibility = View.VISIBLE
                    binding.rvEvent.visibility = View.GONE
                } else {
                    binding.tvNoEvent.visibility = View.GONE
                    binding.rvEvent.visibility = View.VISIBLE
                    adapter.submitList(eventData)
                }
            }
            is Result.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.tvNoEvent.visibility = View.VISIBLE
                Toast.makeText(
                    context,
                    result.error,
                    Toast.LENGTH_SHORT
                ).show()
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
                    observeFinishedEvents()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    observeFinishedEvents()
                }
                return true
            }
        })
    }

    private fun searchEvents(query: String) {
        finishedViewModel.searchFinishedEvents(query).observe(viewLifecycleOwner) { result ->
            handleSearchResult(result, eventAdapter)
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
