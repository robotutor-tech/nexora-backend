package com.robotutor.nexora.shared.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation

interface Identifier: ValueObject {
    val value: String
}

open class ResourceId(override val value: String) : Identifier {
    object ALL : ResourceId("*")

    init {
        validation(value.isBlank()) { "Resource id must not be blank" }
    }


    override fun equals(other: Any?): Boolean {
        return other is ResourceId && other.value == value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
