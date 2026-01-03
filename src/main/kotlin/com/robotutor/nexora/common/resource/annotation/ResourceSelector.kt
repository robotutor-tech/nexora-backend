package com.robotutor.nexora.common.resource.annotation

import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceType

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ResourceSelector(
    val action: ActionType,
    val resourceType: ResourceType
)
