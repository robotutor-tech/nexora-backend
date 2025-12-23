package com.robotutor.nexora.shared.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import java.util.UUID

data class FeedId(override val value: String) : Identifier, ValueObject() {
    init {
        validate()
    }

    override fun validate() {
        validation(value.isNotBlank()) { "Actor id must not be blank" }
    }

    companion object {
        fun generate() = FeedId(value = UUID.randomUUID().toString())
    }

    override fun toString(): String {
        return value
    }
}