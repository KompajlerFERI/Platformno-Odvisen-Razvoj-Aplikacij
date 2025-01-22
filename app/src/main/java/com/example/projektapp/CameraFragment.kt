package com.example.projektapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projektapp.databinding.FragmentCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    private lateinit var outputDirectory: File
    private lateinit var cameraControl: CameraControl
    private var isUsingFrontCamera = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.btnTakePhoto.setOnClickListener { takePhoto() }
        binding.btnSwitchCamera.setOnClickListener {
            isUsingFrontCamera = !isUsingFrontCamera
            startCamera()
        }
        binding.btnFlash.setOnClickListener { toggleFlashMode() }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireContext().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return mediaDir ?: requireContext().filesDir
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val cameraSelector = if (isUsingFrontCamera) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

            imageCapture = ImageCapture.Builder()
                .setFlashMode(ImageCapture.FLASH_MODE_OFF) // Default flash mode
                .build()

            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageCapture
                )
                cameraControl = camera.cameraControl
            } catch (e: Exception) {
                Log.e("CameraFragment", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val photoFile = createImageFile() ?: return

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                requireActivity().runOnUiThread {
                    val originalBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    val adjustedBitmap = if (isUsingFrontCamera) {
                        rotateBitmap(originalBitmap, 180f) // Rotate by 180 degrees for front camera
                    } else {
                        originalBitmap
                    }
                    val bundle = Bundle().apply {
                        putParcelable("capturedImage", adjustedBitmap)
                    }
                    findNavController().navigate(R.id.action_cameraFragment_to_confirmPhotoFragment, bundle)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraFragment", "Error capturing image: ${exception.message}")
                if (isAdded) {
                    Toast.makeText(requireContext(), "Image capture failed: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = android.graphics.Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


    private fun createImageFile(): File? {
        return try {
            val photoDirectory = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ProjectApp")
            if (!photoDirectory.exists()) photoDirectory.mkdirs()
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            File(photoDirectory, "IMG_$timestamp.jpg")
        } catch (e: Exception) {
            Log.e("CameraFragment", "Error creating photo file: ${e.message}")
            null
        }
    }

    private fun toggleFlashMode() {
        when (imageCapture.flashMode) {
            ImageCapture.FLASH_MODE_OFF -> {
                imageCapture.flashMode = ImageCapture.FLASH_MODE_ON
                cameraControl.enableTorch(true)
            }
            ImageCapture.FLASH_MODE_ON -> {
                imageCapture.flashMode = ImageCapture.FLASH_MODE_AUTO
                cameraControl.enableTorch(false)
            }
            ImageCapture.FLASH_MODE_AUTO -> {
                imageCapture.flashMode = ImageCapture.FLASH_MODE_OFF
                cameraControl.enableTorch(false)
            }
        }
        Toast.makeText(
            requireContext(),
            "Flash mode: ${when (imageCapture.flashMode) {
                ImageCapture.FLASH_MODE_ON -> "On"
                ImageCapture.FLASH_MODE_AUTO -> "Auto"
                else -> "Off"
            }}",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraExecutor.shutdown()
    }
}