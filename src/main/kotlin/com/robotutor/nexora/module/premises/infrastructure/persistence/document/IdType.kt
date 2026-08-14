package com.robotutor.nexora.module.premises.infrastructure.persistence.document

import com.robotutor.nexora.shared.persistence.document.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    PREMISE_ID(8)
}
