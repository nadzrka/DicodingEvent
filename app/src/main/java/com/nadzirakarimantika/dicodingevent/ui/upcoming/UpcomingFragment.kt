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
import com.nadzirakarimantika.dicodingevent.data.Result
import com.nadzirakarimantika.dicodingevent.data.local.entity.EventEntity
import com.nadzirakarimantika.dicodingevent.databinding.FragmentFinishedBinding
import com.nadzirakarimantika.dicodingevent.databinding.FragmentUpcomingBinding
import com.nadzirakarimantika.dicodingevent.ui.DetailActivity
import com.nadzirakarimantika.dicodingevent.ui.EventAdapter
import com.nadzirakarimantika.dicodingevent.ui.ViewModelFactory
import com.nadzirakarimantika.dicodingevent.ui.upcoming.UpcomingViewModel

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventAdapter: EventAdapter
    private lateinit var upcomingViewModel: UpcomingViewModel

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

        eventAdapter = EventAdapter { event ->
            navigateToDetailEvent(event)
        }

        binding.rvEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvent.adapter = eventAdapter

        setupSearchView()

        val factory = ViewModelFactory.getInstance(requireActivity())
        upcomingViewModel = viewModels<UpcomingViewModel> { factory }.value

        upcomingViewModel.findUpcomingEvents().observe(viewLifecycleOwner) { result ->
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
                        eventAdapter.submitList(eventData)
                    }
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvEvent.visibility = View.VISIBLE
                    Toast.makeText(
                        context,
                        result.error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        upcomingViewModel.findUpcomingEvents()
    }

    private fun observeSearchUpcomingEvents(query: String) {
        upcomingViewModel.searchUpcomingEvents(query).observe(viewLifecycleOwner) { result ->
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
                        eventAdapter.submitList(eventData)
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvEvent.visibility = View.GONE
                    binding.tvNoEvent.visibility = View.VISIBLE
                    Toast.makeText(
                        context,
                        result.error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupSearchView() {
        val searchView = binding.searchView
        searchView.visibility = View.VISIBLE
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    observeSearchUpcomingEvents(query)
                } else {
                    observeSearchUpcomingEvents("")
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    observeSearchUpcomingEvents("")
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
