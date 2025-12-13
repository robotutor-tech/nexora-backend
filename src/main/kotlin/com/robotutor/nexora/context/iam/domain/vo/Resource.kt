package com.robotutor.nexora.context.iam.domain.vo

import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.model.ResourceId
import com.robotutor.nexora.shared.domain.model.ResourceType

data class Resource(
    val id: ResourceId,
    val premisesId: PremisesId,
    val type: ResourceType
)
