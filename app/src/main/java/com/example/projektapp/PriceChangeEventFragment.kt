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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Call the mqttClient function to configure and make the request
        binding.btnConfirm.setOnClickListener {
            mqttClientConnect()
        }
    }

    // Function to configure and use OkHttpClient with SSLContext
    private fun mqttClientConnect() {
        CoroutineScope(Dispatchers.Main).launch {
            val sslSocketFactory = getSSLSocketFactory()

            if (sslSocketFactory != null) {
                val mqttUrl = "ssl://mqtt-kompajler.northeurope-1.ts.eventgrid.azure.net:8443"
                val clientId = "KompajlerEvent"
                val persistence = MemoryPersistence()

                try {
                    val mqttClient = MqttClient(mqttUrl, clientId, persistence)
                    val options = MqttConnectOptions().apply {
                        socketFactory = sslSocketFactory
                        isCleanSession = true
                        mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1
                        userName = "KompajlerEvent"
                    }

                    mqttClient.connect(options)
                    if (mqttClient.isConnected) {
                        Toast.makeText(requireContext(), "Connected successfully", Toast.LENGTH_LONG).show()
                        Log.d("Price change event", "Successfully connected")
                    } else {
                        Toast.makeText(requireContext(), "Connection failed", Toast.LENGTH_SHORT).show()
                        Log.d("Price change event", "Connection failed")
                    }
                } catch (e: MqttException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Connection error: ${e.message}", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Unexpected error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "SSL/TLS configuration error", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun getSSLSocketFactory(): javax.net.ssl.SSLSocketFactory? {
        try {
            val clientCertificateInputStream = resources.openRawResource(R.raw.kompajlerevent) // Your .p12 client certificate file
            val password = BuildConfig.MQTT_PASSWORD.toCharArray() // Password for the .p12 file

            val trustStoreInputStream = resources.openRawResource(R.raw.truststore) // Your .bks truststore file
            val trustStorePassword = BuildConfig.MQTT_PASSWORD1.toCharArray() // Password for the .bks file

            // Load the client certificate (PKCS#12)
            val keyStore = KeyStore.getInstance("PKCS12").apply {
                load(clientCertificateInputStream, password)
            }

            // Load the truststore (BKS)
            val trustStore = KeyStore.getInstance("BKS").apply {
                load(trustStoreInputStream, trustStorePassword)
            }

            // Initialize KeyManagerFactory for the client certificate
            val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply {
                init(keyStore, password)
            }

            // Initialize TrustManagerFactory for the TrustStore
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
                init(trustStore)
            }

            // Initialize SSLContext with both KeyManager and TrustManager
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(keyManagerFactory.keyManagers, trustManagerFactory.trustManagers, null)

            return sslContext.socketFactory
        } catch (e: Exception) {
            e.printStackTrace()
            return null // Return null if there is an error
        }
    }
}
