package com.robotutor.nexora.module.premises.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.ValueObject

data class Address(val street: Street, val postalCode: PostalCode) : ValueObject

@JvmInline
value class Street private constructor(val value: String) {
    init {
        validation(value.isBlank()) { "Street must not be blank" }
    }

    companion object {
        fun of(value: String): Street {
            return Street(value.trim())
        }
    }
}

@JvmInline
value class PostalCode private constructor(val value: String) {
    init {
        validation(value.isBlank()) { "Postal code must not be blank" }
        validation(value.length != 6) { "Postal code must be 6 digits" }
    }

    companion object {
        fun of(value: String): PostalCode {
            return PostalCode(value.trim())
        }
    }
}
