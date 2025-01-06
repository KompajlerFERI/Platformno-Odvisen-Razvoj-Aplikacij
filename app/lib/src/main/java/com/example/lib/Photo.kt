package com.example.lib

class Photo(
    val URL: String
) {
    companion object {
        fun generateRandom(): Photo {
            return Photo("/images/defaultRestaurantPhoto")
        }
    }
}