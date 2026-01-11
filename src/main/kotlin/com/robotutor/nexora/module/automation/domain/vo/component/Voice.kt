package com.robotutor.nexora.module.automation.domain.vo.component

import com.robotutor.nexora.module.automation.domain.vo.component.data.ComponentData
import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.ValueObject

data class Voice(val commands: List<String>) : Trigger, ComponentData<Voice>, ValueObject {
    init {
        validation(commands.isEmpty()) { "Voice commands must not be empty" }
        validation(commands.any { it.isBlank() }) { "Voice commands must not contain blank values" }
        validation(commands.distinct().size != commands.size) { "Voice commands must not contain duplicate values" }
    }
}