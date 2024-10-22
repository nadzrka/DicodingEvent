@file:Suppress("unused", "RedundantSuppression")

package com.nadzirakarimantika.dicodingevent.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nadzirakarimantika.dicodingevent.data.Result
import com.nadzirakarimantika.dicodingevent.data.local.entity.EventEntity
import com.nadzirakarimantika.dicodingevent.databinding.FragmentFavoriteBinding
import com.nadzirakarimantika.dicodingevent.ui.BaseFragment
import com.nadzirakarimantika.dicodingevent.ui.DetailActivity
import com.nadzirakarimantika.dicodingevent.ui.EventAdapter
import com.nadzirakarimantika.dicodingevent.ui.ViewModelFactory

class FavoriteFragment : BaseFragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventAdapter: EventAdapter
    private val favoriteViewModel: FavoriteViewModel by viewModels { ViewModelFactory.getInstance(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkInternetConnection()

        eventAdapter = EventAdapter { event ->
            navigateToDetailEvent(event)
        }

        binding.rvEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvent.adapter = eventAdapter

        observeFavoriteEvents()

    }

    private fun observeFavoriteEvents() {
        favoriteViewModel.findFavoriteEvent().observe(viewLifecycleOwner) { result ->
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
                binding.rvEvent.visibility = View.GONE
                Toast.makeText(
                    context,
                    result.error,
                    Toast.LENGTH_SHORT
                ).show()
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
