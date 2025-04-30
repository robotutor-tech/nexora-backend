package com.robotutor.nexora.device.models

import com.robotutor.nexora.security.services.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    DEVICE_ID(12),
}
