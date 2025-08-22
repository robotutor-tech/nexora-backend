package com.robotutor.nexora.modules.iam.domain.model

import com.robotutor.nexora.shared.domain.model.IdSequenceType


enum class IdType(override val length: Int) : IdSequenceType {
    ACTOR_ID(10),
    ENTITLEMENT_ID(16),
    ROLE_ID(10),
}