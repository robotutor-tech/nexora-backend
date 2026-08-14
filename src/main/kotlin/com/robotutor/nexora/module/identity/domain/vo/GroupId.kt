package com.robotutor.nexora.module.identity.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.Identifier
import java.util.*

data class GroupId(override val value: String) : Identifier {
    init {
        validation(value.isBlank()) { "Group id must not be blank" }
    }

    companion object {
        fun generate() = GroupId(value = UUID.randomUUID().toString())
    }
}
