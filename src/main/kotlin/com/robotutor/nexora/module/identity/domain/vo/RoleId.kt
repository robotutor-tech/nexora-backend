package com.robotutor.nexora.module.identity.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.Identifier
import java.util.*

data class RoleId(override val value: String) : Identifier {
    init {
        validation(value.isBlank()) { "Role id must not be blank" }
    }

    companion object {
        fun generate() = RoleId(value = UUID.randomUUID().toString())
    }
}
