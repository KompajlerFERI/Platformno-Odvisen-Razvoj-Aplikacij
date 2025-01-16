package com.example.projektapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.projektapp.databinding.FragmentDialogBinding

class PopUpWindowFragment : DialogFragment() {
    private var _binding: FragmentDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the restaurant name from arguments
        val restaurantName = arguments?.getString("restaurantName")

        // Set the restaurant name to the TextView
        binding.restaurantName.text = restaurantName
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            resources.getDimensionPixelSize(R.dimen.popup_width),
            resources.getDimensionPixelSize(R.dimen.popup_height)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
