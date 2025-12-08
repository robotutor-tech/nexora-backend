package com.robotutor.nexora.shared.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation

data class Name(val value: String) : ValueObject() {
    init {
        validate()
    }

    override fun validate() {
        validation(value.trim().length in 4..30) {
            "Name must be between 4 and 30 characters long"
        }
    }
}