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
import com.nadzirakarimantika.dicodingevent.databinding.FragmentUpcomingBinding
import com.nadzirakarimantika.dicodingevent.ui.DetailActivity
import com.nadzirakarimantika.dicodingevent.ui.EventAdapter
import com.nadzirakarimantika.dicodingevent.ui.ViewModelFactory

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private val upcomingViewModel: UpcomingViewModel by viewModels { ViewModelFactory.getInstance(requireActivity()) }
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

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: UpcomingViewModel by viewModels {
            factory
        }

        eventAdapter = EventAdapter { event -> navigateToDetailEvent(event)}
        binding.rvEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvent.adapter = eventAdapter

        setupSearchView()

        viewModel.findUpcomingEvents().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val eventData = result.data
                        eventAdapter.submitList(eventData)
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            context,
                            result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
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
                    upcomingViewModel.searchFinishedEvents(query)
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
