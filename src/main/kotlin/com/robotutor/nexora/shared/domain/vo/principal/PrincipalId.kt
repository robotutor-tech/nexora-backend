package com.robotutor.nexora.shared.domain.vo.principal

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.Identifier
import com.robotutor.nexora.shared.domain.vo.ValueObject

data class PrincipalId(override val value: String) : Identifier, ValueObject {
    init {
        validation(value.isBlank()) { "Principal id must not be blank" }
    }

    override fun toString(): String = value
}