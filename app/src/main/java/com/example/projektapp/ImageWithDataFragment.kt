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
import android.os.Handler
import android.os.Looper
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.http.Body
import java.io.File
import java.io.FileOutputStream
import kotlin.math.ceil
import kotlin.random.Random
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request

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
    private val handler = Handler(Looper.getMainLooper())
    private var simulationRunnable: Runnable? = null
    private var totalTimer: CountDownTimer? = null
    private var intervalTimer: CountDownTimer? = null
    private var restaurantName: String = ""
    private var restaurantId: String = ""

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

        val bundle = arguments
        if (bundle != null) {
            restaurantName = bundle.getString("restaurantName") ?: ""
            restaurantId = bundle.getString("restaurantId") ?: ""
            binding.simRestaurantName.text = restaurantName;
        }

        binding.btnBack.setOnClickListener {
            totalTimer?.cancel()
            intervalTimer?.cancel()
            simulationRunnable?.let { handler.removeCallbacks(it) }
            findNavController().navigate(R.id.action_dataSimulatorFragment_to_restaurantsFragment)
        }

        binding.inputSimulationTimer.addTextChangedListener(inputWatcher)
        binding.inputSimulationInterval.addTextChangedListener(inputWatcher)

        binding.btnSimulate.setOnClickListener {
            val timerText = binding.inputSimulationTimer.text.toString()
            val intervalText = binding.inputSimulationInterval.text.toString()

            if (timerText.isNotEmpty() && intervalText.isNotEmpty()) {
                val timerMillis = timerText.toLong() * 60 * 1000
                val intervalMillis = intervalText.toLong() * 60 * 1000
                startSimulation(timerMillis, intervalMillis)
            } else {
                Toast.makeText(context, "Please enter valid times", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val inputWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val timerText = binding.inputSimulationTimer.text.toString().trim()
            val intervalText = binding.inputSimulationInterval.text.toString().trim()
            binding.btnSimulate.isEnabled = timerText.isNotEmpty() && intervalText.isNotEmpty()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun startSimulation(durationMillis: Long, intervalMillis: Long) {
        val endTime = System.currentTimeMillis() + durationMillis

        totalTimer?.cancel()
        intervalTimer?.cancel()

        totalTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                binding.timerTotal.text = String.format("Total Time: %02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.timerTotal.text = "Total Time: 00:00"
            }
        }.start()

        simulationRunnable = object : Runnable {
            override fun run() {
                if (System.currentTimeMillis() < endTime) {
                    val randomImageResId = getRandomImageResId()
                    if (randomImageResId != null) {
                        binding.imgPicked.setImageResource(randomImageResId)
                        val bitmap = BitmapFactory.decodeResource(resources, randomImageResId)
                        uploadImageToServer(bitmap)
                    } else {
                        Toast.makeText(context, "No images found", Toast.LENGTH_SHORT).show()
                    }

                    intervalTimer = object : CountDownTimer(intervalMillis, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            val minutes = (millisUntilFinished / 1000) / 60
                            val seconds = (millisUntilFinished / 1000) % 60
                            binding.timerInterval.text = String.format("Interval Time: %02d:%02d", minutes, seconds)
                        }

                        override fun onFinish() {
                            binding.timerInterval.text = "Interval Time: 00:00"
                        }
                    }.start()

                    handler.postDelayed(this, intervalMillis)
                }
            }
        }
        handler.post(simulationRunnable!!)
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
                    val responseBody = response.body()?.string()?.toDoubleOrNull()
                    if (responseBody != null) {
                        val roundedValue = ceil(responseBody).toInt()
                        binding.lblPredictedAmount.text = "Predicted Amount: $roundedValue"
                        updateLastCapacity(restaurantId, roundedValue)
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

    fun updateLastCapacity(restaurantId: String, roundedValue: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val url = "http://13.95.23.193:3001/restaurants/$restaurantId/lastCapacity?lastCapacity=$roundedValue"
            val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), "")

            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .build()

            try {
                val response = client.newCall(request).execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        println("Update successful: ${response.body?.string()}")
                    } else {
                        println("Update failed: ${response.code}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println("Exception: ${e.message}")
                }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        //handler.removeCallbacks(simulationRunnable!!)
        totalTimer?.cancel()
        intervalTimer?.cancel()
    }
}