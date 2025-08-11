package com.robotutor.nexora.modules.user.domain.model

import com.robotutor.nexora.shared.domain.model.IdSequenceType

enum class IdType(override val length: Int) : IdSequenceType {
    USER_ID(10),
}
