package com.example.projektapp

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.projektapp.databinding.FragmentConfirmPhotoBinding

class ConfirmPhotoFragment : Fragment() {
    private var _binding: FragmentConfirmPhotoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConfirmPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Safely retrieving the captured image
        val capturedImage = arguments?.getParcelable<Bitmap>("capturedImage")
        if (capturedImage != null) {
            Log.d("ConfirmPhotoFragment", "Captured image received, size: ${capturedImage.width}x${capturedImage.height}")
            binding.imgCaptured.setImageBitmap(capturedImage)
        } else {
            Log.e("ConfirmPhotoFragment", "Captured image is null")
            Toast.makeText(requireContext(), "Image not available", Toast.LENGTH_SHORT).show()
        }

        binding.btnConfirm.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("capturedImage", capturedImage)
            }
            // findNavController().navigate(R.id.action_confirmPhotoFragment_to_dataSimulatorFragment, bundle)
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigate(R.id.action_confirmPhotoFragment_to_restaurantsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
