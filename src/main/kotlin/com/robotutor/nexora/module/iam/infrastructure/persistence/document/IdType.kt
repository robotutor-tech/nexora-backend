package com.robotutor.nexora.module.iam.infrastructure.persistence.document

import com.robotutor.nexora.common.persistence.document.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    ACCOUNT_ID(10)
}