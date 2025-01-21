/*
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
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.projektapp.databinding.FragmentDialogBinding

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

        // Retrieve the restaurant name from arguments
        val restaurantName = arguments?.getString("restaurantName")
        val restaurantId = arguments?.getString("restaurantId")
        val openFromImageWithData = arguments?.getBoolean("openFromImageWithData")
        println(arguments)

        // Set the restaurant name to the TextView
        binding.restaurantName.text = restaurantName

        binding.btnCamera.setOnClickListener {
            // ne dela, če je program zagnan na emulatorju
            @Override
            fun onClick(v: View?) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_CODE)
            }
            if (openFromImageWithData!!) findNavController().popBackStack()
            findNavController().navigate(R.id.action_restaurantsFragment_to_dataSimulatorFragment)
        }

        binding.btnGalery.setOnClickListener {

        }

        binding.btnSubscribe.setOnClickListener {
            MqttClientHandler.connect()
            MqttClientHandler.subscribe("price")
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

        binding.btnEvent.setOnClickListener {
            val bundle = Bundle().apply {
                putString("restaurantName", restaurantName)
                putString("restaurantId", restaurantId)
            }
            findNavController().navigate(R.id.action_restaurantsFragment_to_priceChangeEventFragment, bundle)
        }

        binding.btnBack.setOnClickListener {
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
            resources.getDimensionPixelSize(R.dimen.popup_width),
            resources.getDimensionPixelSize(R.dimen.popup_height)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
*/

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

        // Retrieve the restaurant name from arguments
        val restaurantName = arguments?.getString("restaurantName")
        val restaurantId = arguments?.getString("restaurantId")
        val openFromImageWithData = arguments?.getBoolean("openFromImageWithData")
        println(arguments)

        // Set the restaurant name to the TextView
        binding.tvDetectCapacityDescription.text = getString(R.string.detect_capacity_description, restaurantName?.lowercase())

        binding.btnCamera.setOnClickListener {
            // ne dela, če je program zagnan na emulatorju
            @Override
            fun onClick(v: View?) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_CODE)
            }
            if (openFromImageWithData!!) findNavController().popBackStack()
            findNavController().navigate(R.id.action_restaurantsFragment_to_dataSimulatorFragment)
        }

        binding.btnGalery.setOnClickListener {

        }

        binding.btnSubscribe.setOnClickListener {
            MqttClientHandler.connect()
            MqttClientHandler.subscribe("price")
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

        binding.btnEvent.setOnClickListener {
            val bundle = Bundle().apply {
                putString("restaurantName", restaurantName)
                putString("restaurantId", restaurantId)
            }
            findNavController().navigate(R.id.action_restaurantsFragment_to_priceChangeEventFragment, bundle)
        }

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
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_corners)
        val margin = resources.getDimensionPixelSize(R.dimen.margin_medium)
        dialog?.window?.decorView?.setPadding(margin, margin, margin, margin)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}