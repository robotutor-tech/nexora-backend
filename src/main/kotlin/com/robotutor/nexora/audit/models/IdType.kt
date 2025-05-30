package com.robotutor.nexora.audit.models

import com.robotutor.nexora.security.services.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    AUDIT_ID(16),
}