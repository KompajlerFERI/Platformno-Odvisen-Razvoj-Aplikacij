package com.example.projektapp

import com.example.lib.Restaurant
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import okhttp3.ResponseBody

interface APIInterface {
    @GET("/restaurants")
    suspend fun getRestaurants(): Response<List<Restaurant>>

    @POST("PeopleRecognizer")
    fun uploadImage(@Body image: RequestBody): Call<ResponseBody>

    @PUT("restaurants/{resId}/lastCapacity")
    fun updateLastCapacity(
        @Path("resId") resId: String,
        @Query("lastCapacity") lastCapacity: Int
    ): Call<ResponseBody>
}
