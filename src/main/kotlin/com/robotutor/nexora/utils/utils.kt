package com.robotutor.nexora.utils

import kotlin.reflect.full.memberProperties

inline fun <reified T : Any> T.toMap(): Map<String, Any?> {
    return T::class.memberProperties.associate { prop ->
        prop.name to prop.get(this)
    }
}
