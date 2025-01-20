package com.example.projektapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.lang.ref.WeakReference

object MqttClientHandler {
    private const val hostname = "fb8c9bba468848348cfa18678ea11f96.s1.eu.hivemq.cloud"
    private const val username = "aljosa1-client"
    private const val passwordClient = "Admin123"
    private const val clientId = "asd"
    private const val CHANNEL_ID = "mqtt_channel"

    private val mqttClient = MqttAsyncClient("ssl://$hostname:8883", clientId, MemoryPersistence())
    private val options = MqttConnectOptions().apply {
        this.userName = username
        this.password = passwordClient.toCharArray()
    }

    private var contextRef: WeakReference<Context>? = null

    fun setContext(context: Context) {
        this.contextRef = WeakReference(context)
    }

    init {
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
                contextRef?.get()?.let { context ->
                    showNotification(context, "New MQTT Message", message.toString())
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                println("Message delivery complete.")
            }
        })
    }

    fun connect() {
        try {
            mqttClient.connect(options).waitForCompletion()
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

    fun publish(topic: String, message: String) {
        try {
            val mqttMessage = MqttMessage(message.toByteArray())
            mqttMessage.qos = 1
            mqttClient.publish(topic, mqttMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showNotification(context: Context, title: String, message: String) {
        createNotificationChannel(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_utensils)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MQTT Channel"
            val descriptionText = "Channel for MQTT messages"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}