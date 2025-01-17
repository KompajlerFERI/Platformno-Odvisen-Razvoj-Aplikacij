package com.example.projektapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.projektapp.databinding.FragmentSimulateDataBinding
import android.widget.ImageView
import android.widget.Toast
import kotlin.random.Random
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import android.graphics.BitmapFactory
import android.util.Base64
import android.content.res.Resources
import android.graphics.Bitmap
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.http.Body
import java.io.File
import java.io.FileOutputStream

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
                val bitmap = BitmapFactory.decodeResource(resources, randomImageResId)
                uploadImageToServer(bitmap)
            } else {
                Toast.makeText(context, "No images found", Toast.LENGTH_SHORT).show()
            }
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

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    binding.lblPredictedAmount.text = "Predicted Amount: $responseBody"
                    Toast.makeText(context, "Received response: $responseBody", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Unsuccessful response: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
        })
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
