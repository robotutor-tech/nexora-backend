package com.robotutor.nexora.context.user.domain.vo

import com.robotutor.nexora.shared.domain.vo.ValueObject
import com.robotutor.nexora.shared.domain.utility.validation
import java.util.UUID


data class UserId(val value: String) : ValueObject() {
    init {
        validate()
    }

    override fun validate() {
        validation(value.isNotBlank()) { "User id must not be blank" }
    }

    companion object {
        fun generate() = UserId(value = UUID.randomUUID().toString())
    }
}