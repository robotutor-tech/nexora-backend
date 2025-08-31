package com.robotutor.nexora.shared.infrastructure.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.springframework.http.HttpMethod

class HttpMethodSerializer : JsonSerializer<HttpMethod>() {
    override fun serialize(value: HttpMethod, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.name())
    }
}