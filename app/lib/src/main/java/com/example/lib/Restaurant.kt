package com.example.lib

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

class Restaurant(
    var name: String,
    var mealPrice: BigDecimal,
    var mealSurcharge: BigDecimal,
    var lastCapacity: Int,
    var maxCapacity: Int,
    var id: String?
) {

    companion object {
        fun generateRandom(): Restaurant {
            val names = listOf("The Food Place", "Gourmet Hub", "Dine Fine", "Eatery Delight")
            val name = names.random()
            val mealPrice = BigDecimal(Random.nextDouble(5.0, 50.0)).setScale(2, RoundingMode.HALF_UP)
            val mealSurcharge = BigDecimal(Random.nextDouble(1.0, 10.0)).setScale(2, RoundingMode.HALF_UP)
            val lastCapacity = Random.nextInt(0, 100)
            val maxCapacity = Random.nextInt(100, 200)
            val id = Random.nextInt(1000, 9999).toString()

            return Restaurant(name, mealPrice, mealSurcharge, lastCapacity, maxCapacity, id)
        }
    }
}