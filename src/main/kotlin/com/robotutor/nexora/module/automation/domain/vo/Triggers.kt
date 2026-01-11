package com.robotutor.nexora.module.automation.domain.vo

import com.robotutor.nexora.module.automation.domain.vo.component.Trigger
import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.ValueObject

data class Triggers(val values: List<Trigger>) : ValueObject {
    init {
        validation(values.isEmpty()) { "Triggers must not be empty" }
        validation(values.distinct().size != values.size) { "Triggers must not contain duplicate values" }
    }
}
