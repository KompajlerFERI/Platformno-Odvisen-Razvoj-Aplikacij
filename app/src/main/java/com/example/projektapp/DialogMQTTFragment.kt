package com.example.projektapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projektapp.databinding.FragmentDialogMqttBinding

class DialogMQTTFragment : Fragment() {
    private var _binding: FragmentDialogMqttBinding? = null
    private val binding get() = _binding!!

    private val application: MyApplication
        get() = requireActivity().application as MyApplication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogMqttBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val restaurantName = arguments?.getString("restaurantName")
        val restaurantId = arguments?.getString("restaurantId")
        val openFromImageWithData = arguments?.getBoolean("openFromImageWithData")

        binding.btnEvent.setOnClickListener {
            val bundle = Bundle().apply {
                putString("restaurantName", restaurantName)
                putString("restaurantId", restaurantId)
            }
            findNavController().navigate(R.id.action_restaurantsFragment_to_priceChangeEventFragment, bundle)
        }
        if (application.subscribed) {
            binding.btnSubscribe.setText(R.string.unsubscribe)
        }
        else {
            binding.btnSubscribe.setText(R.string.subscribe)
        }

        binding.btnSubscribe.setOnClickListener {
            if (!application.subscribed) {
                application.connect()
                application.subscribe("price")
                application.subscribed = true
                binding.btnSubscribe.setText(R.string.unsubscribe)
            }
            else {
                application.unsubscribe("price")
                application.disconnect()
                application.subscribed = false
                binding.btnSubscribe.setText(R.string.subscribe)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}