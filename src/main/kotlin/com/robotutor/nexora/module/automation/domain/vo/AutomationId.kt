package com.robotutor.nexora.module.automation.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.Identifier
import com.robotutor.nexora.shared.domain.vo.ValueObject

data class AutomationId(override val value: String) : Identifier, ValueObject {
    init {
        validation(value.isBlank()) { "Automation id must not be blank" }
    }

    companion object {
        fun generate(): AutomationId {
            return AutomationId(value = java.util.UUID.randomUUID().toString())
        }
    }
}
