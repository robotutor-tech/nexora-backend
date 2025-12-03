package com.robotutor.nexora.context.user.domain.vo

import com.robotutor.nexora.shared.domain.ValueObject
import com.robotutor.nexora.shared.domain.utility.validation

data class Email(val value: String, val isVerified: Boolean = false) : ValueObject() {
    override fun validate() {
        val regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$"
        validation(Regex(regex, RegexOption.IGNORE_CASE).matches(value)) { "Email must be valid" }
    }
}
