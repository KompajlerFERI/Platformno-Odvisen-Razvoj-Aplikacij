package com.example.projektapp

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.projektapp.databinding.FragmentDialogBinding
import com.google.android.material.tabs.TabLayoutMediator

class PopUpWindowFragment : DialogFragment() {
    private var _binding: FragmentDialogBinding? = null
    private val binding get() = _binding!!

    private val application: MyApplication
        get() = requireActivity().application as MyApplication

    companion object {
        const val REQUEST_CODE = 22
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = Bundle().apply {
            putString("restaurantName", arguments?.getString("restaurantName"))
            putString("restaurantId", arguments?.getString("restaurantId"))
            putBoolean("openFromImageWithData", arguments?.getBoolean("openFromImageWithData") ?: false)
        }

        val adapter = SlidesAdapter(requireActivity(), data)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()
        binding.dotsIndicator.attachTo(binding.viewPager)

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                val bundle = Bundle().apply {
                    putParcelable("capturedImage", imageBitmap)
                }
                findNavController().navigate(R.id.action_restaurantsFragment_to_dataSimulatorFragment, bundle)
            } else {
                Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Unsuccessful", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_corners)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}