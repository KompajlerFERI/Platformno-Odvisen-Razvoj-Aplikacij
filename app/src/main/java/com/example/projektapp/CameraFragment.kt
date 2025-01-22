package com.example.projektapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
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

        imageCapture = ImageCapture.Builder().build()
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.btnTakePhoto.setOnClickListener {
            takePhoto()
        }
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
            File(it, resources.getString(R.string.app_name)).apply { it.mkdirs() }
        }
        return mediaDir ?: requireContext().filesDir
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Initialize preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            // Initialize ImageCapture
            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraFragment", "Use case binding failed", e)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = this.imageCapture
        if (!::imageCapture.isInitialized) {
            Log.e("CameraFragment", "ImageCapture is not initialized")
            return
        }

        val photoFile = createImageFile() ?: return // Make sure photoFile is not null

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions, cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    requireActivity().runOnUiThread {
                        try {
                            Log.d("CameraFragment", "Photo saved successfully")
                            // Convert saved image file to Bitmap
                            val bitmapOptions = BitmapFactory.Options().apply {
                                inJustDecodeBounds = false
                            }
                            val capturedBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

                            Log.d("CameraFragment", "Bitmap created, size: ${capturedBitmap?.width}x${capturedBitmap?.height}")
                            val bundle = Bundle().apply {
                                putParcelable("capturedImage", capturedBitmap)
                            }
                            // Pass the Bitmap via Bundle
                            findNavController().navigate(R.id.action_cameraFragment_to_confirmPhotoFragment, bundle)
                        } catch (e: Exception) {
                            Log.e("CameraFragment", "Error saving image: ${e.message}")
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraFragment", "Error capturing image: ${exception.message}")
                    activity?.runOnUiThread {
                        if (isAdded) {
                            Toast.makeText(
                                requireContext(),
                                "Image capture failed: ${exception.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            })

    }

    private fun createImageFile(): File? {
        return try {
            val photoDirectory = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ProjectApp")
            if (!photoDirectory.exists()) photoDirectory.mkdirs()

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            File(photoDirectory, "IMG_${timestamp}.jpg")
        } catch (e: Exception) {
            Log.e("CameraFragment", "Error creating photo file: ${e.message}")
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraExecutor.shutdown()
    }
}