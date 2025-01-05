package com.example.projektapp

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lib.Restaurant
import com.example.projektapp.databinding.FragmentRestaurantsBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException


class RestaurantsFragment : Fragment() {
    private var _binding: FragmentRestaurantsBinding? = null
    private val binding get() = _binding!!

    private lateinit var restaurants: List<Restaurant>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRestaurantsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restaurants = listOf()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // I N I T I A L   D A T A   F E T C H
        viewLifecycleOwner.lifecycleScope.launch {
            showLoading(true)
            val response = fetchRestaurants()
            if (response != null) {
                restaurants = response
                binding.recyclerView.adapter = RestaurantAdapter(restaurants)
            }
            showLoading(false)
        }

        val typedValue = TypedValue()
        val theme = requireContext().theme
        theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)
        binding.swipeRefreshLayout.setColorSchemeResources(typedValue.resourceId)

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val response = fetchRestaurants()
                if (response != null) {
                    restaurants = response
                    binding.recyclerView.adapter = RestaurantAdapter(restaurants)
                    binding.recyclerView.scrollToPosition(0)
                }
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private suspend fun fetchRestaurants(): List<Restaurant>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getRestaurants()
                if (response.isSuccessful) {
                    Timber.i("Fetched: ${response.body()}")
                    response.body()
                } else {
                    handleError("HTTP Error: ${response.message()}")
                    null
                }
            } catch (e: IOException) {
                handleError("Error: ${e.message}")
                null
            } catch (e: HttpException) {
                handleError("HTTP Error: ${e.message}")
                null
            }
        }
    }

    private suspend fun handleError(message: String) {
        withContext(Dispatchers.Main) {
            Timber.e(message)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}