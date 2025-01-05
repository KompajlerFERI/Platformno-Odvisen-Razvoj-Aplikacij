package com.example.lib

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class LocationDeserializer : JsonDeserializer<Location> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Location {
        val jsonObject = json.asJsonObject
        val coordinates = jsonObject.getAsJsonArray("coordinates")
        val longitude = coordinates[0].asDouble
        val latitude = coordinates[1].asDouble
        return Location(longitude, latitude)
    }
}