package com.robotutor.nexora.shared.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import java.util.UUID

data class ActorId(override val value: String) : Identifier, ValueObject {
    init {
        validation(value.isBlank()) { "Actor id must not be blank" }
    }

    companion object {
        fun generate() = ActorId(value = UUID.randomUUID().toString())
    }

    override fun toString(): String {
        return value
    }
}