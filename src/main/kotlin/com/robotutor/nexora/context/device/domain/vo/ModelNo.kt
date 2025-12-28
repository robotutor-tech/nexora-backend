package com.robotutor.nexora.context.device.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.ValueObject

data class ModelNo(val value: String) : ValueObject {
    init {
        validation(value.isBlank()) { "Model no must not be blank" }
    }

}