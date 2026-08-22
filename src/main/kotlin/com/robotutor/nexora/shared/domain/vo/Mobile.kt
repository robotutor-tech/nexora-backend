package com.robotutor.nexora.shared.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation

@JvmInline
value class Mobile private constructor(val value: String) : ValueObject {
    init {
        validation(!Regex("\\d{10}$").matches(value)) { "Mobile must be valid" }
    }

    companion object {
        fun of(value: String): Mobile {
            return Mobile(value.trim())
        }
    }

}
