package com.robotutor.nexora.shared.logger.serializer

object DefaultSerializer {
    fun serialize(obj: Any?): String {
        return ObjectMapperCache.objectMapper.writeValueAsString(obj)
    }

    fun <T> deserialize(str: String, type: Class<T>): T {
        return ObjectMapperCache.objectMapper.readValue<T>(str, type)
    }

    fun toMap(obj: Any): Map<String, Any?> {
        @Suppress("UNCHECKED_CAST")
        return ObjectMapperCache.objectMapper.convertValue(obj, Map::class.java) as Map<String, Any?>
    }
}