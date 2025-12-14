package com.robotutor.nexora.shared.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation

enum class ResourceType {
    AUTOMATION,
    AUTOMATION_RULE,
    DEVICE,
    FEED,
    INVITATION,
    PREMISES,
    WIDGET,
    ZONE,
}

enum class ActionType {
    READ,
    CONTROL,
    WRITE,
    DELETE,
}

open class ResourceId(val value: String) : ValueObject() {
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