package com.robotutor.nexora.context.user.infrastructure.persistence.document

import com.robotutor.nexora.shared.domain.model.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    USER_ID(8)
}