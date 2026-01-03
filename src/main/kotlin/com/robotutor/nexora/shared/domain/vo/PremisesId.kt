package com.robotutor.nexora.shared.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation

data class PremisesId(override val value: String) : Identifier, ValueObject {
    init {
        validation(value.isBlank()) { "PremisesId must not be blank" }
    }

    override fun toString(): String {
        return "{ value: $value }"
    }
}