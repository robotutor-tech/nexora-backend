package com.robotutor.nexora.context.premises.infrastructure.persistence.document

import com.robotutor.nexora.shared.infrastructure.persistence.document.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    PREMISE_ID(8)
}