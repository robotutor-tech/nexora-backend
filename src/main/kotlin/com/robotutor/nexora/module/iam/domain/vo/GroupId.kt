package com.robotutor.nexora.module.iam.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.Identifier
import com.robotutor.nexora.shared.domain.vo.ValueObject
import java.util.UUID

data class GroupId(override val value: String) : Identifier, ValueObject {
    init {
        validation(value.isBlank()) { "Group id must not be blank" }
    }

    companion object {
        fun generate() = GroupId(value = UUID.randomUUID().toString())
    }
}
