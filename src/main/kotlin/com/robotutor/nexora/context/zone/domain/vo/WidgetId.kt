package com.robotutor.nexora.context.zone.domain.vo

import com.robotutor.nexora.shared.domain.vo.Identifier
import com.robotutor.nexora.shared.domain.vo.ValueObject

data class WidgetId(override val value: String) : Identifier, ValueObject {
    companion object {
        fun generate(): WidgetId {
            return WidgetId(value = java.util.UUID.randomUUID().toString())
        }
    }
}
