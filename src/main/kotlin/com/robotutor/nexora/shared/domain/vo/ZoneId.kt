package com.robotutor.nexora.shared.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import java.util.UUID

data class ZoneId(override val value: String) : Identifier, ValueObject {
    init {
        validation(value.isBlank()) { "Zone id must not be blank" }
    }

    companion object {
        fun generate() = ZoneId(value = UUID.randomUUID().toString())
    }
}