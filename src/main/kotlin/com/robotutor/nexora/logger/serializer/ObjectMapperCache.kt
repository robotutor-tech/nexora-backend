package com.robotutor.nexora.logger.serializer

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.Instant

object ObjectMapperCache {
    val objectMapper: Gson = GsonBuilder()
        .registerTypeAdapter(Instant::class.java, InstantAdapter())
        .create()
}