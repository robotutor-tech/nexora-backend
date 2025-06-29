package com.robotutor.nexora.logger.serializer

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.time.Instant

class InstantAdapter : TypeAdapter<Instant>() {

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Instant?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.toString())
        }
    }

    @Throws(IOException::class)
    override fun read(jsonReader: JsonReader): Instant? {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull()
            return null
        }
        val dateString = jsonReader.nextString()
        return Instant.parse(dateString)
    }
}