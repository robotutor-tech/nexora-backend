package com.robotutor.nexora.context.iam.interfaces.controller.view

import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceType

data class ResourceRequest(
    val resourceType: ResourceType,
    val actionType: ActionType,
    val resourceId: String
)

data class ResourceResponse(
    val isAuthorized: Boolean
)