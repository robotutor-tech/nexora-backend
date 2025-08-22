package com.robotutor.nexora.modules.premises.domain.model

import com.robotutor.nexora.shared.domain.model.IdSequenceType


enum class IdType(override val length: Int) : IdSequenceType {
    PREMISE_ID(8)
}