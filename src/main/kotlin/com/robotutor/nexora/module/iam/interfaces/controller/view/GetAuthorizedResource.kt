package com.robotutor.nexora.module.iam.interfaces.controller.view

import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceSelector
import com.robotutor.nexora.shared.domain.vo.ResourceType

data class GetAuthorizedResourcesRequest(
    val resourceType: ResourceType,
    val actionType: ActionType,
)

data class GetAuthorizedResourcesResponse(
    val premisesId: String,
    val resourceType: ResourceType,
    val actionType: ActionType,
    val resourceSelector: ResourceSelector,
    val allowedIds: Set<String>,
    val deniedIds: Set<String>
)