package com.robotutor.nexora.shared.application.serialization

object DefaultSerializer {
    fun serialize(obj: Any?): String {
        return ObjectMapperCache.objectMapper.writeValueAsString(obj)
    }

    fun <T> deserialize(str: String, type: Class<T>): T {
        return ObjectMapperCache.objectMapper.readValue<T>(str, type)
    }
}