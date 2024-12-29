package com.example.projektapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.projektapp.databinding.FragmentRestaurantsBinding


class RestaurantsFragment : Fragment() {
    private var _binding: FragmentRestaurantsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRestaurantsBinding.inflate(inflater, container, false)
        return binding.root
    }
}