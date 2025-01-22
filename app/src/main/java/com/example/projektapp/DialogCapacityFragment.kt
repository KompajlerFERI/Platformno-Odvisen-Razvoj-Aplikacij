package com.example.projektapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projektapp.databinding.FragmentDialogCapacityBinding
import java.io.IOException
import java.io.InputStream

class DialogCapacityFragment : Fragment() {
    private var _binding: FragmentDialogCapacityBinding? = null
    private val binding get() = _binding!!

    private val application: MyApplication
        get() = requireActivity().application as MyApplication

    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val imageBitmap = getRotatedBitmap(it)
                val bundle = Bundle().apply {
                    putParcelable("capturedImage", imageBitmap)
                }
                findNavController().navigate(R.id.action_restaurantsFragment_to_confirmPhotoFragment, bundle)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

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
            selectImage.launch("image/*")
            if (openFromImageWithData!!) findNavController().popBackStack()
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

    private fun getRotatedBitmap(uri: Uri): Bitmap {
        val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        return rotateBitmap(bitmap, 270f)
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = android.graphics.Matrix().apply {
            postRotate(degrees)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}

