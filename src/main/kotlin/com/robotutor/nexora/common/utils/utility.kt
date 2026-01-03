package com.robotutor.nexora.common.utils

import com.robotutor.nexora.shared.application.serialization.DefaultSerializer

inline fun <reified T : Any> T.toMap(): Map<*, *> {
    val serialize = DefaultSerializer.serialize(this)
    return DefaultSerializer.deserialize(serialize, Map::class.java)
}