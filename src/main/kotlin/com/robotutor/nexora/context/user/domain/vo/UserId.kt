package com.robotutor.nexora.context.user.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.Identifier
import com.robotutor.nexora.shared.domain.vo.ValueObject
import java.util.*

data class UserId(override val value: String) : Identifier, ValueObject {
    init {
        validation(value.isBlank()) { "User id must not be blank" }
    }

    companion object {
        fun generate() = UserId(value = UUID.randomUUID().toString())
    }
}