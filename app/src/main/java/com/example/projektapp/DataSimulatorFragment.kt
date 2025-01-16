package com.example.projektapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.projektapp.databinding.FragmentSimulateDataBinding
import android.content.res.Resources
import android.widget.ImageView
import android.widget.Toast
import kotlin.random.Random

class DataSimulatorFragment : Fragment() {
    private var _binding: FragmentSimulateDataBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSimulateDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_dataSimulatorFragment_to_restaurantsFragment)
        }

        binding.btnSimulate.setOnClickListener {
            val randomImageResId = getRandomImageResId()
            if (randomImageResId != null) {
                binding.imgPicked.setImageResource(randomImageResId)
            } else {
                Toast.makeText(context, "No images found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getRandomImageResId(): Int? {
        val drawables = getDrawableResources()
        val imgDrawables = drawables.filter { it.startsWith("img") }
        return if (imgDrawables.isNotEmpty()) {
            val randomIndex = Random.nextInt(imgDrawables.size)
            resources.getIdentifier(imgDrawables[randomIndex], "drawable", requireContext().packageName)
        } else {
            null
        }
    }

    private fun getDrawableResources(): List<String> {
        val fields = R.drawable::class.java.declaredFields
        return fields.mapNotNull { field ->
            try {
                field.get(null) as? Int
                field.name
            } catch (e: Exception) {
                null
            }
        }
    }
}
