package com.robotutor.nexora.utils

import com.robotutor.nexora.shared.logger.serializer.DefaultSerializer

inline fun <reified T : Any> T.toMap(): Map<String, Any?> {
    return DefaultSerializer.toMap(this)
}
