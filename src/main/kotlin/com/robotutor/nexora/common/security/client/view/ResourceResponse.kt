package com.robotutor.nexora.common.security.client.view

import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ResourceSelector
import com.robotutor.nexora.shared.domain.vo.ResourceType

data class ResourceResponse(
    val premisesId: String,
    val resourceType: ResourceType,
    val actionType: ActionType,
    val resourceSelector: ResourceSelector,
    val allowedIds: Set<String>,
    val deniedIds: Set<String>
)