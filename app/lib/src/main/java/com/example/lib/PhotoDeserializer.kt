package com.example.lib

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class PhotoDeserializer : JsonDeserializer<Photo> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Photo {
        val jsonObject = json.asJsonObject
        val photoURL = jsonObject.get("imagePath").asString
        return Photo(photoURL)
    }
}