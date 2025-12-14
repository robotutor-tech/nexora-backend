package com.robotutor.nexora.shared.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation

data class PremisesId(val value: String) : ValueObject() {
    init {
        validate()
    }

    override fun validate() {
        validation(value.isNotBlank()) { "PremisesId must not be blank" }
    }

    override fun toString(): String {
        return value
    }
}