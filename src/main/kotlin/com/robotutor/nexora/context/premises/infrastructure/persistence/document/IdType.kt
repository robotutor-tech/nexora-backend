package com.robotutor.nexora.context.premises.infrastructure.persistence.document

import com.robotutor.nexora.common.persistence.mongo.document.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    PREMISE_ID(8)
}