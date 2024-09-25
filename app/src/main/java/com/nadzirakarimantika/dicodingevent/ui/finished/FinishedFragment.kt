package com.nadzirakarimantika.dicodingevent.ui.finished

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nadzirakarimantika.dicodingevent.data.response.ListEventsItem
import com.nadzirakarimantika.dicodingevent.databinding.FragmentFinishedBinding
import com.nadzirakarimantika.dicodingevent.ui.DetailActivity
import com.nadzirakarimantika.dicodingevent.ui.EventAdapter

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private val finishedViewModel: FinishedViewModel by viewModels()

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

        binding.rvEvent.layoutManager = LinearLayoutManager(requireContext())

       finishedViewModel.listEvents.observe(viewLifecycleOwner, Observer { listEvents ->
            val adapter = EventAdapter(listEvents) { event ->
                // Handle the click event here, e.g., navigate to the detail screen
                navigateToDetailEvent(event)
            }
            binding.rvEvent.adapter = adapter
        })

        // Observe loading state to show/hide progress bar
        finishedViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })

        finishedViewModel.findEvent()
    }

    private fun navigateToDetailEvent(event: ListEventsItem) {
        // Start DetailActivity with event.id as an extra
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_EVENT_ID, event.id) // Pass the event ID
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}