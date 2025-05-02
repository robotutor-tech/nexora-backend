package com.robotutor.nexora.auth.models

import com.robotutor.nexora.security.services.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    TOKEN_ID(16),
    DEVICE_INVITATION_ID(10),
    USER_INVITATION_ID(10),
}
