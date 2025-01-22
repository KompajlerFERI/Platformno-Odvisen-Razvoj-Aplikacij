package com.example.projektapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.projektapp.PopUpWindowFragment.Companion.REQUEST_CODE
import com.example.projektapp.databinding.FragmentDialogCapacityBinding

class DialogCapacityFragment : Fragment() {
    private var _binding: FragmentDialogCapacityBinding? = null
    private val binding get() = _binding!!

    private val application: MyApplication
        get() = requireActivity().application as MyApplication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogCapacityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val restaurantName = arguments?.getString("restaurantName")
        val restaurantId = arguments?.getString("restaurantId")
        val openFromImageWithData = arguments?.getBoolean("openFromImageWithData")

        binding.tvDetectCapacityDescription.text = getString(R.string.detect_capacity_description, restaurantName?.lowercase())

        binding.btnCamera.setOnClickListener {
            if (openFromImageWithData!!) findNavController().popBackStack()
            findNavController().navigate(R.id.action_restaurantsFragment_to_cameraFragment)
        }

        binding.btnGalery.setOnClickListener {
            // TODO
        }

        binding.btnSimulateData.setOnClickListener {
            val randomImageResId = application.getRandomImageResId(requireContext())
            if (randomImageResId != null) {
                val bitmap = BitmapFactory.decodeResource(resources, randomImageResId)
                val bundle = Bundle().apply {
                    putParcelable("capturedImage", bitmap)
                    putInt("randomImageResId", randomImageResId)
                    putString("restaurantName", restaurantName)
                    putString("restaurantId", restaurantId)
                }
                if (openFromImageWithData!!) findNavController().popBackStack()
                findNavController().navigate(R.id.action_restaurantsFragment_to_dataSimulatorFragment, bundle)
            } else {
                Toast.makeText(context, "No images found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}