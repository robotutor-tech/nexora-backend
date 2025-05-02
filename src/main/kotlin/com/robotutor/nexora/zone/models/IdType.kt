package com.robotutor.nexora.zone.models

import com.robotutor.nexora.security.services.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    ZONE_ID(8)
}