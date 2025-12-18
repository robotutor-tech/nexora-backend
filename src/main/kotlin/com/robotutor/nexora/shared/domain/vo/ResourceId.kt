package com.robotutor.nexora.shared.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation

interface Identifier {
    val value: String
}

open class ResourceId(override val value: String) : Identifier, ValueObject() {
    object ALL : ResourceId("*")

    init {
        validate()
    }

    override fun validate() {
        validation(value.isNotBlank()) { "Resource id must not be blank" }
    }


    override fun equals(other: Any?): Boolean {
        return other is ResourceId && other.value == value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}