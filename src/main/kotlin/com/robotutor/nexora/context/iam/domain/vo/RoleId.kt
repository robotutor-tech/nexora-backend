package com.robotutor.nexora.context.iam.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.Identifier
import com.robotutor.nexora.shared.domain.vo.ValueObject
import java.util.UUID

data class RoleId(override val value: String) : Identifier, ValueObject {
    init {
        validation(value.isBlank()) { "Role id must not be blank" }
    }

    companion object {
        fun generate() = RoleId(value = UUID.randomUUID().toString())
    }
}
