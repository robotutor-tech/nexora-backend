package com.robotutor.nexora.context.user.domain.vo

import com.robotutor.nexora.shared.domain.vo.ValueObject
import com.robotutor.nexora.shared.domain.utility.validation

data class Mobile(val value: String, val isVerified: Boolean = false) : ValueObject {
    init {
        validation(!Regex("\\d{10}$").matches(value)) { "Mobile must be valid" }
    }

}
