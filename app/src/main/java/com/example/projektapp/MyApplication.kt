package com.example.projektapp

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.example.lib.Restaurant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class MyApplication : Application() {
    lateinit var restaurants: List<Restaurant>

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
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

    suspend fun handleError(context: Context, message: String,) {
        withContext(Dispatchers.Main) {
            Timber.e(message)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}