package com.robotutor.nexora.user.models

import com.robotutor.nexora.security.services.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    USER_ID(10),
}
