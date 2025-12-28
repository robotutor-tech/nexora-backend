package com.robotutor.nexora.context.iam.infrastructure.persistence.document

import com.robotutor.nexora.common.persistence.mongo.document.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    ACCOUNT_ID(10)
}