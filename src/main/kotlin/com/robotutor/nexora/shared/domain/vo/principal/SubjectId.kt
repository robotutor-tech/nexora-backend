package com.robotutor.nexora.shared.domain.vo.principal

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.Identifier

open class SubjectId(override val value: String) : Identifier {
    init {
        validation(value.isBlank()) { "Principal id must not be blank" }
    }

    override fun toString(): String = value
}

enum class SubjectType {
    USER,
    DEVICE
}
