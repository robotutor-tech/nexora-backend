package com.robotutor.nexora.modules.zone.models

import com.robotutor.nexora.common.security.services.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    ZONE_ID(8)
}