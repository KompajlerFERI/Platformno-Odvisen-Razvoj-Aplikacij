package com.example.lib

import kotlin.random.Random

class Location(
    val longitude: Double,
    val latitude: Double,
) {
    companion object {
        fun generateRandom(): Location {
            return Location(Random.nextDouble(-180.0, 180.0), Random.nextDouble(-90.0, 90.0))
        }
    }
}