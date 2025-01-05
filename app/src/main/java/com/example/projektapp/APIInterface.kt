package com.example.projektapp

import com.example.lib.Restaurant
import retrofit2.Response
import retrofit2.http.GET

interface APIInterface {
    @GET("/restaurants")
    suspend fun getRestaurants():Response<List<Restaurant>>
}