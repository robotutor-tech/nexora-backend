package com.robotutor.nexora.shared.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation

data class AccountId(override val value: String) : Identifier, ValueObject {
    init {
        validation(value.isBlank()) { "Account id must not be blank" }
    }

    override fun toString(): String {
        return value
    }
}