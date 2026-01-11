package com.robotutor.nexora.module.automation.domain.vo

import com.robotutor.nexora.module.automation.domain.vo.component.Action
import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.ValueObject

data class Actions(val values: List<Action>) : ValueObject {
    init {
        validation(values.isEmpty()) { "Triggers must not be empty" }
    }
}
