package com.robotutor.nexora.modules.zone.domain.model

import com.robotutor.nexora.shared.domain.model.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    ZONE_ID(8)
}