package com.robotutor.nexora.context.user.domain.vo

import com.robotutor.nexora.shared.domain.ValueObject
import com.robotutor.nexora.shared.domain.utility.validation

data class Mobile(val value: String, val isVerified: Boolean = false) : ValueObject() {
    override fun validate() {
        val regex = "\\d{10}$"
        validation(Regex(regex).matches(value)) { "Mobile must be valid" }
    }
}
