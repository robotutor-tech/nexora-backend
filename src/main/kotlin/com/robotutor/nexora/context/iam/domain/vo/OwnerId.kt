package com.robotutor.nexora.context.iam.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.Identifier
import com.robotutor.nexora.shared.domain.vo.ValueObject

data class OwnerId(override val value: String) : Identifier, ValueObject() {
    init {
        validate()
    }

    override fun validate() {
        validation(value.isNotBlank()) { "Owner id must not be blank" }
    }

    override fun toString(): String = value
}

