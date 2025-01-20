package com.example.projektapp

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

object MqttClientHandler {
    private const val hostname = "fb8c9bba468848348cfa18678ea11f96.s1.eu.hivemq.cloud"
    private const val username = "aljosa1-client"
    private const val passwordClient = "Admin123"
    private const val clientId = "asd"

    private val mqttClient = MqttAsyncClient("ssl://$hostname:8883", clientId, MemoryPersistence())
    private val options = MqttConnectOptions().apply {
        this.userName = username
        this.password = passwordClient.toCharArray()
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
}