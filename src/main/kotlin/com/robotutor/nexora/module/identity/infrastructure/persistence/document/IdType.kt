package com.robotutor.nexora.module.identity.infrastructure.persistence.document

import com.robotutor.nexora.shared.persistence.document.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    ACCOUNT_ID(10)
}
