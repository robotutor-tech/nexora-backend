package com.robotutor.nexora.context.premises.infrastructure.persistence.document

import com.robotutor.nexora.shared.domain.model.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    PREMISE_ID(8)
}