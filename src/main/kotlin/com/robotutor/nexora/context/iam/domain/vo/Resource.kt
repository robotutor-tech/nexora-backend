package com.robotutor.nexora.context.iam.domain.vo

import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.domain.vo.ResourceType

data class Resource(
    val resourceId: ResourceId,
    val premisesId: PremisesId,
    val type: ResourceType,
    val action: ActionType
)
