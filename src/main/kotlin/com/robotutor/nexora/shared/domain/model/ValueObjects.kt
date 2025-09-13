package com.robotutor.nexora.shared.domain.model

import com.robotutor.nexora.shared.domain.validation

data class ModelNo(val value: String)
data class SerialNo(val value: String)
data class Name(val value: String) {
    init {
        validation(value.trim().length in 4..30) {
            "Name must be between 4 and 30 characters long"
        }
    }
}