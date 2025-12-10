package com.robotutor.nexora.shared.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import java.util.UUID

data class ActorId(val value: String) : ValueObject() {
    init {
        validate()
    }

    override fun validate() {
        validation(value.isNotBlank()) { "Actor id must not be blank" }
    }

    companion object {
        fun generate() = ActorId(value = UUID.randomUUID().toString())
    }

    override fun toString(): String {
        return value
    }
}