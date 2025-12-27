package com.robotutor.nexora.shared.interfaces.annotation

import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceType


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class HttpAuthorize(
    val action: ActionType,
    val resource: ResourceType,
    val selector: String = "ALL"
)

