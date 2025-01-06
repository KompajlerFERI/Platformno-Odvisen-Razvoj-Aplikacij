package com.example.projektapp

import com.example.lib.Location
import com.example.lib.LocationDeserializer
import com.example.lib.Photo
import com.example.lib.PhotoDeserializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val API_URL = BuildConfig.API_URL

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Location::class.java, LocationDeserializer())
        .registerTypeAdapter(Photo::class.java, PhotoDeserializer())
        .create()

    val api: APIInterface by lazy {
        Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(APIInterface::class.java)
    }
}