package com.robotutor.nexora.auth.models

import com.robotutor.nexora.utils.services.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    TOKEN_ID(16),
}
