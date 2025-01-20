@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nadzirakarimantika.dicodingevent.data.Result
import com.nadzirakarimantika.dicodingevent.data.local.entity.EventEntity
import com.nadzirakarimantika.dicodingevent.databinding.FragmentHomeBinding
import com.nadzirakarimantika.dicodingevent.ui.BaseFragment
import com.nadzirakarimantika.dicodingevent.ui.DetailActivity
import com.nadzirakarimantika.dicodingevent.ui.ViewModelFactory

class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding? get() = _binding
    private val homeViewModel: HomeViewModel by viewModels { ViewModelFactory.getInstance(requireActivity()) }
    private lateinit var eventHorizontalAdapter: EventHorizontalAdapter
    private lateinit var eventVerticalAdapter: EventVerticalAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkInternetConnection()
        setupRecyclerViews()
        setupSearchView()
        observeEvents()
    }

    private fun setupRecyclerViews() {
        eventHorizontalAdapter = EventHorizontalAdapter { navigateToDetailEvent(it) }
        binding?.rvUpcoming?.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = eventHorizontalAdapter
        }

        eventVerticalAdapter = EventVerticalAdapter { navigateToDetailEvent(it) }
        binding?.rvEvent?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = eventVerticalAdapter
        }
    }

    private fun setupSearchView() {
        binding?.searchView?.apply {
            visibility = View.VISIBLE
            setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    (query.orEmpty())
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    observeSearchUpcomingEvents(newText.orEmpty())
                    observeSearchFinishedEvents(newText.orEmpty())
                    return true
                }
            })
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
                binding?.apply {
                    tvNoEvent.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    progressBar2.visibility = View.VISIBLE
                }

            }
            is Result.Success -> {
                val eventData = result.data.take(5)
                binding?.apply {
                    tvNoEvent.visibility = if (result.data.isEmpty()) View.VISIBLE else View.GONE
                    progressBar.visibility = View.GONE
                }
               adapter.submitList(eventData)
            }
            is Result.Error -> {
                binding?.apply {
                    progressBar.visibility = View.GONE
                    tvNoEvent.visibility = View.GONE
                }
            }
        }
    }

    private fun observeSearchUpcomingEvents(query: String) {
        homeViewModel.searchUpcomingEvents(query).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding?.apply {
                        progressBar.visibility = View.VISIBLE
                        tvNoEvent.visibility = View.GONE
                    }
                }

                is Result.Success -> {
                    binding?.progressBar?.visibility = View.GONE
                    val eventData = result.data.take(5)
                    if (eventData.isEmpty()) {
                        binding?.apply {
                            tvNoEvent.visibility = View.VISIBLE
                            rvUpcoming.visibility = View.GONE
                        }
                    } else {
                        binding?.apply {
                            tvNoEvent.visibility = View.GONE
                            rvUpcoming.visibility = View.VISIBLE
                        }
                        eventHorizontalAdapter.submitList(eventData)
                    }
                }

                is Result.Error -> {
                    binding?.apply {
                        progressBar.visibility = View.GONE
                        rvUpcoming.visibility = View.GONE
                        tvNoEvent.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun observeSearchFinishedEvents(query: String) {
        homeViewModel.searchFinishedEvents(query).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding?.apply {
                        progressBar2.visibility = View.VISIBLE
                        tvNoEvent2.visibility = View.GONE
                    }
                }

                is Result.Success -> {
                    binding?.progressBar2?.visibility = View.GONE
                    val eventData = result.data.take(5)
                    if (eventData.isEmpty()) {
                        binding?.apply {
                            tvNoEvent2.visibility = View.VISIBLE
                            rvEvent.visibility = View.GONE
                        }
                    } else {
                        binding?.apply {
                            tvNoEvent2.visibility = View.GONE
                            rvEvent.visibility = View.VISIBLE
                        }
                        eventVerticalAdapter.submitList(eventData)
                    }
                }

                is Result.Error -> {
                    binding?.apply {
                        progressBar2.visibility = View.GONE
                        rvEvent.visibility = View.GONE
                        tvNoEvent2.visibility = View.VISIBLE
                    }
                }
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
