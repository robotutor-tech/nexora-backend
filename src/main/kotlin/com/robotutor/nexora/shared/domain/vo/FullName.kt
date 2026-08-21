package com.robotutor.nexora.shared.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation

data class FullName(val value: String) : ValueObject {
    init {
        validation(value.trim().length !in 2..30) { "Name must be between 2 and 30 characters long" }
    }
}
