package com.robotutor.nexora.module.automation.domain.vo.component

import com.robotutor.nexora.shared.domain.vo.ValueObject

sealed interface Component {
    val type: ComponentType
}

sealed interface Action : ValueObject, Component
sealed interface Trigger : ValueObject, Component
sealed interface Condition : ValueObject, Component

enum class ComponentType {
    AUTOMATION,
    FEED_CONTROL,
    FEED_VALUE,
    VOICE,
    WAIT,
}