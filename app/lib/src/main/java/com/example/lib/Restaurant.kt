package com.example.lib

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

class Restaurant(
    var id: String,
    var name: String,
    var photo: Photo,
    var location: Location,
    var mealPrice: BigDecimal,
    var mealSurcharge: BigDecimal,
    var lastCapacity: Int,
    var maxCapacity: Int,
) {

    override fun toString(): String {
        return "Restaurant(id=$id, name='$name', photoURL: ${photo.URL} location=(lat:${location.latitude}, lon: ${location.longitude}) mealPrice=$mealPrice, mealSurcharge=$mealSurcharge, lastCapacity=$lastCapacity, maxCapacity=$maxCapacity)"
    }

    companion object {
        fun generateRandom(): Restaurant {
            val names = listOf("The Food Place", "Gourmet Hub", "Dine Fine", "Eatery Delight")
            val id = Random.nextInt(1000, 9999).toString()
            val name = names.random()
            val photo = Photo.generateRandom()
            val location = Location.generateRandom()
            val mealPrice = BigDecimal(Random.nextDouble(5.0, 50.0)).setScale(2, RoundingMode.HALF_UP)
            val mealSurcharge = BigDecimal(Random.nextDouble(1.0, 10.0)).setScale(2, RoundingMode.HALF_UP)
            val lastCapacity = Random.nextInt(0, 100)
            val maxCapacity = Random.nextInt(100, 200)

            return Restaurant(id, name, photo, location, mealPrice, mealSurcharge, lastCapacity, maxCapacity)
        }
    }
}