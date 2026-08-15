package com.robotutor.nexora.module.user.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.ValueObject

data class Name(val value: String) : ValueObject {
    init {
        validation(value.trim().length !in 2..30) { "Name must be between 2 and 30 characters long" }
    }
}
