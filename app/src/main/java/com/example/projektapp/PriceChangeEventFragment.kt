package com.example.projektapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.projektapp.databinding.FragmentEventBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.io.IOException
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

class PriceChangeEventFragment : Fragment() {
    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    private val application: MyApplication
        get() = requireActivity().application as MyApplication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val restaurantName = arguments?.getString("restaurantName")
        val restaurantId = arguments?.getString("restaurantId")

        binding.simRestaurantName.text = restaurantName

        binding.btnConfirm.setOnClickListener {
            val newPriceText = binding.txtNewPrice.text.toString()
            val newPrice = newPriceText.toFloatOrNull()
            if (newPriceText.isNotEmpty() && restaurantName != null && restaurantId != null) {
                val message = "$restaurantName|$newPrice|$restaurantId"
                application.connect()
                application.publish("price", message, restaurantId, newPrice!!)
                application.disconnect()
            } else {
                Toast.makeText(requireContext(), "Please enter a price and ensure restaurant details are available", Toast.LENGTH_SHORT).show()
            }
        }
    }
}