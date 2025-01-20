@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.ui.finished

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nadzirakarimantika.dicodingevent.data.Result
import com.nadzirakarimantika.dicodingevent.data.local.entity.EventEntity
import com.nadzirakarimantika.dicodingevent.databinding.FragmentFinishedBinding
import com.nadzirakarimantika.dicodingevent.ui.BaseFragment
import com.nadzirakarimantika.dicodingevent.ui.DetailActivity
import com.nadzirakarimantika.dicodingevent.ui.EventAdapter
import com.nadzirakarimantika.dicodingevent.ui.ViewModelFactory

class FinishedFragment : BaseFragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding: FragmentFinishedBinding? get() = _binding
    private lateinit var eventAdapter: EventAdapter
    private val finishedViewModel: FinishedViewModel by viewModels { ViewModelFactory.getInstance(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkInternetConnection()
        setupRecyclerView()
        setupSearchView()
        observeFinishedEvents()
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter { navigateToDetailEvent(it) }
        binding?.rvEvent?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = eventAdapter
        }
    }

    private fun setupSearchView() {
        binding?.searchView?.apply {
            visibility = View.VISIBLE
            setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    observeSearchFinishedEvents(query.orEmpty())
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    observeSearchFinishedEvents(newText.orEmpty())
                    return true
                }
            })
        }
    }

    private fun observeFinishedEvents() {
        finishedViewModel.findFinishedEvent().observe(viewLifecycleOwner) { result ->
            handleResult(result)
        }
    }

    private fun observeSearchFinishedEvents(query: String) {
        finishedViewModel.searchFinishedEvents(query).observe(viewLifecycleOwner) { result ->
            handleResult(result)
        }
    }

    private fun handleResult(result: Result<List<EventEntity>>) {
        when (result) {
            is Result.Loading -> showLoading(true)
            is Result.Success -> {
                showLoading(false)
                updateEventList(result.data)
            }
            is Result.Error -> showError()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding?.tvNoEvent?.visibility = (if (isLoading) View.GONE else binding?.tvNoEvent?.visibility)!!
    }

    private fun updateEventList(eventData: List<EventEntity>) {
        if (eventData.isEmpty()) {
            binding?.apply {
                tvNoEvent.visibility = View.VISIBLE
                rvEvent.visibility = View.GONE
            }

        } else {
            binding?.apply {
                tvNoEvent.visibility = View.GONE
                rvEvent.visibility = View.VISIBLE
            }
            eventAdapter.submitList(eventData)
        }
    }

    private fun showError() {
        binding?.apply {
            progressBar.visibility = View.GONE
            rvEvent.visibility = View.GONE
            tvNoEvent.visibility = View.VISIBLE
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
