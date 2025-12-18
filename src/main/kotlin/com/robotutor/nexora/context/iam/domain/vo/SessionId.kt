package com.robotutor.nexora.context.iam.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.Identifier
import com.robotutor.nexora.shared.domain.vo.ValueObject
import java.util.UUID

data class SessionId(override val value: String) : Identifier, ValueObject() {
    init {
        validate()
    }

    companion object {
        fun generate(): SessionId {
            return SessionId(UUID.randomUUID().toString())
        }
    }

    override fun validate() {
        validation(value.isNotBlank()) { "Token id must not be blank" }
    }
}


