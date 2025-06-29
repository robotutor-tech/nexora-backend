package com.robotutor.nexora.saga.models

import com.robotutor.nexora.security.services.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    SagaId(12)
}