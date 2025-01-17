package com.example.projektapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.projektapp.databinding.FragmentImageWithDataBinding
import android.widget.Toast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.POST
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.http.Body
import java.io.File
import java.io.FileOutputStream
import kotlin.math.ceil

interface AzureApiService {
    @POST("PeopleRecognizer")
    fun uploadImage(@Body image: RequestBody): Call<ResponseBody>
}


object RetrofitClient {
    private const val BASE_URL = "https://neuralnetworkkompajler.azurewebsites.net/api/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    val instance: AzureApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(AzureApiService::class.java)
    }
}

class ImageWithDataFragment : Fragment() {
    private var _binding: FragmentImageWithDataBinding? = null
    private val binding get() = _binding!!

    private val application: MyApplication
        get() = requireActivity().application as MyApplication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImageWithDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val restaurantName = arguments?.getString("restaurantName")
        var restaurantId = arguments?.getString("restaurantId")
        var capturedImage = arguments?.getParcelable<Bitmap>("capturedImage")
        val randomImageIndex = arguments?.getInt("randomImageResId")
        checkIfImageIsCaptured(capturedImage, randomImageIndex)

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_dataSimulatorFragment_to_restaurantsFragment)
        }

        binding.btnSimulate.setOnClickListener {
            val dialogFragment = PopUpWindowFragment()

            val bundle = Bundle()
            bundle.putString("restaurantName", restaurantName)
            bundle.putString("restaurantId", restaurantId)
            bundle.putBoolean("openFromImageWithData", true)
            dialogFragment.arguments = bundle

            dialogFragment.show(childFragmentManager, "PopUpWindowFragment")

            if (dialogFragment.dialog?.isShowing == true) {
                restaurantId = arguments?.getString("restaurantId")
                capturedImage = arguments?.getParcelable<Bitmap>("capturedImage")
                checkIfImageIsCaptured(capturedImage, randomImageIndex)
            }
        }
    }

    private fun checkIfImageIsCaptured(capturedImage: Bitmap?, randomImageIndex: Int?) {
        if (capturedImage != null) {
            binding.imgPicked.setImageBitmap(capturedImage)
            uploadImageToServer(capturedImage)
        } else if (randomImageIndex != null) {
            binding.imgPicked.setImageResource(randomImageIndex)
            val bitmap = BitmapFactory.decodeResource(resources, randomImageIndex)
            uploadImageToServer(bitmap)
        } else {
            Toast.makeText(context, "No images found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToServer(bitmap: Bitmap) {
        val file = File(context?.cacheDir, "temp_image.jpg")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val call = RetrofitClient.instance.uploadImage(requestBody)

        Toast.makeText(context, "Uploading image...", Toast.LENGTH_SHORT).show()
        binding.progressBar.visibility = View.VISIBLE

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                t.printStackTrace()
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()?.toDoubleOrNull()
                    if (responseBody != null) {
                        val roundedValue = ceil(responseBody).toInt()
                        binding.lblPredictedAmount.text = "Predicted Amount: $roundedValue"
                        Toast.makeText(context, "Received response: $roundedValue", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Invalid response format", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Unsuccessful response: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
