package com.robotutor.nexora.shared.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation

data class Mobile(val value: String) : ValueObject {
    init {
        validation(!Regex("\\d{10}$").matches(value)) { "Mobile must be valid" }
    }

}
