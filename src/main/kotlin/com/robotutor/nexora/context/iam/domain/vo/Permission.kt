package com.robotutor.nexora.context.iam.domain.vo

import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ResourceType

data class Permission(
    val action: ActionType,
    val resourceType: ResourceType,
    val selector: ResourceSelector
)

sealed class ResourceSelector {
    object All : ResourceSelector()
    data class ById(val resourceId: String) : ResourceSelector()
    data class ByType(val type: ResourceType) : ResourceSelector()
}
