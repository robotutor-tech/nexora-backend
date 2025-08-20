package com.robotutor.nexora.modules.premises.models

import com.robotutor.nexora.common.security.service.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    PREMISE_ID(8)
}