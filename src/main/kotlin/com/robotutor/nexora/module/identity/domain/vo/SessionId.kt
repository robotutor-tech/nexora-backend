package com.robotutor.nexora.module.identity.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.Identifier
import java.util.*

data class SessionId(override val value: String) : Identifier {
    init {
        validation(value.isBlank()) { "Token id must not be blank" }
    }

    companion object {
        fun generate(): SessionId {
            return SessionId(UUID.randomUUID().toString())
        }
    }

}
