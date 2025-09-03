package com.robotutor.nexora.modules.device.domain.entity

import com.robotutor.nexora.shared.domain.model.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    DEVICE_ID(10),
}