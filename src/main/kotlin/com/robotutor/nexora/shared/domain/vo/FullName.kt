package com.robotutor.nexora.shared.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation

@JvmInline
value class FullName private constructor(val value: String) : ValueObject {
    init {
        validation(value.length !in 2..30) { "Name must be between 2 and 30 characters long" }
    }

    companion object {
        fun of(value: String): FullName {
            return FullName(value.trim())
        }
    }
}
