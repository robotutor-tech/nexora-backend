package com.robotutor.nexora.module.zone.domain.vo

import com.robotutor.nexora.shared.domain.vo.Identifier

data class WidgetId(override val value: String) : Identifier {
    companion object {
        fun generate(): WidgetId {
            return WidgetId(value = java.util.UUID.randomUUID().toString())
        }
    }
}
