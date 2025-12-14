package com.robotutor.nexora.context.zone.domain.entity

import com.robotutor.nexora.shared.domain.model.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    ZONE_ID(8)
}