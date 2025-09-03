package com.robotutor.nexora.modules.user.domain.entity

import com.robotutor.nexora.shared.domain.model.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    USER_ID(10),
}
