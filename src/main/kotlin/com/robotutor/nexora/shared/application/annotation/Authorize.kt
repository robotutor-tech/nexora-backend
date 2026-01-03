package com.robotutor.nexora.shared.application.annotation

import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceSelector
import com.robotutor.nexora.shared.domain.vo.ResourceType

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Authorize(
    val action: ActionType,
    val resource: ResourceType,
    val selector: ResourceSelector = ResourceSelector.ALL,
    val expression: String = ""
)
