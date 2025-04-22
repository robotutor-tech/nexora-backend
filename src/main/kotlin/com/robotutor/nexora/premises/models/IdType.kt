package com.robotutor.nexora.premises.models

import com.robotutor.nexora.utils.services.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    PREMISE_ID(8)
}