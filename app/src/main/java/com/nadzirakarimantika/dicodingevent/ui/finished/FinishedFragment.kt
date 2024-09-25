package com.nadzirakarimantika.dicodingevent.ui.finished

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nadzirakarimantika.dicodingevent.databinding.FragmentFinishedBinding
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
            val adapter = EventAdapter(listEvents)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}