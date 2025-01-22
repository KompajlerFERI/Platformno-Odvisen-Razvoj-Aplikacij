package com.example.projektapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.lib.Restaurant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.lang.ref.WeakReference
import kotlin.random.Random

class MyApplication : Application() {
    lateinit var restaurants: List<Restaurant>
    private val hostname = "fb8c9bba468848348cfa18678ea11f96.s1.eu.hivemq.cloud"
    private val username = "aljosa1-client"
    private val passwordClient = "Admin123"
    private val clientId = "asd"
    private val mqttClient = MqttAsyncClient("ssl://$hostname:8883", clientId, MemoryPersistence())

    private val serverUsername = "aljosa1-server"
    private val passwordServer = "Admin123"
    private val serverId = "asd"
    private val mqttServer = MqttAsyncClient("ssl://$hostname:8883", serverId, MemoryPersistence())
    private val options = MqttConnectOptions().apply {
        this.userName = username
        this.password = passwordClient.toCharArray()
    }
    private val optionsServer = MqttConnectOptions().apply {
        this.userName = username
        this.password = passwordClient.toCharArray()
    }

    private var contextRef: WeakReference<Context>? = null

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        initMqttClient()
    }

    private fun initMqttClient() {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                println("Connected to $serverURI")
                mqttClient.subscribe("price", 1)
            }

            override fun connectionLost(cause: Throwable?) {
                println("Connection lost: ${cause?.message}")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                println("Message received on topic '$topic': ${message.toString()}")
                val parts = message.toString().split("|")
                val restaurantName = parts.getOrNull(0) ?: "Unknown"
                val newPrice = parts.getOrNull(1) ?: "Unknown"
                showNotification(restaurantName, "Price changed to $newPrice€")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                println("Message delivery complete.")
            }
        })
    }

    private fun updateDatabase(restaurantId: String, price: Float) {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val url = "http://13.95.23.193:3001/restaurants/$restaurantId/mealPrice?mealPrice=$price"
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

    fun setContext(context: Context) {
        this.contextRef = WeakReference(context)
    }

    fun connect() {
        try {
            mqttClient.connect(options).waitForCompletion()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun connectServer() {
        try {
            mqttServer.connect(optionsServer).waitForCompletion()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disconnectServer() {
        try {
            mqttServer.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            mqttClient.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun subscribe(topic: String) {
        try {
            mqttClient.subscribe(topic, 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun publish(topic: String, message: String, restaurantId: String, price: Float) {
        try {
            updateDatabase(restaurantId, price)
            val mqttMessage = MqttMessage(message.toByteArray())
            mqttMessage.qos = 1
            mqttServer.publish(topic, mqttMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchRestaurants(context: Context): List<Restaurant>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getRestaurants()
                if (response.isSuccessful) {
                    Timber.i("Fetched: ${response.body()}")
                    response.body()
                } else {
                    handleError(context, "HTTP Error: ${response.message()}")
                    null
                }
            } catch (e: IOException) {
                handleError(context, "Error: ${e.message}")
                null
            } catch (e: HttpException) {
                handleError(context, "HTTP Error: ${e.message}")
                null
            }
        }
    }

    suspend fun handleError(context: Context, message: String) {
        withContext(Dispatchers.Main) {
            Timber.e(message)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    fun getRandomImageResId(context: Context): Int? {
        val drawables = getDrawableResources()
        val imgDrawables = drawables.filter { it.startsWith("img") }
        return if (imgDrawables.isNotEmpty()) {
            val randomIndex = Random.nextInt(imgDrawables.size)
            resources.getIdentifier(imgDrawables[randomIndex], "drawable", context.packageName)
        } else {
            null
        }
    }
    fun getDrawableResources(): List<String> {
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

    fun showNotification(title: String, content: String) {
        val channelId = "mqtt_channel"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "MQTT Message Received",
                NotificationManager.IMPORTANCE_HIGH
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        getSystemService(NotificationManager::class.java).notify(2, notification)
    }
}