package com.robotutor.nexora.shared.application.annotation

import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ResourceType


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequireAccess(
    val action: ActionType,
    val resource: ResourceType,
    val idParam: String = "*"
)

