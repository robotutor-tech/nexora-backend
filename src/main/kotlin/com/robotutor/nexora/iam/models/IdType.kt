package com.robotutor.nexora.iam.models

import com.robotutor.nexora.security.services.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    ACTOR_ID(10),
    ENTITLEMENT_ID(16),
    ROLE_ID(10),
}