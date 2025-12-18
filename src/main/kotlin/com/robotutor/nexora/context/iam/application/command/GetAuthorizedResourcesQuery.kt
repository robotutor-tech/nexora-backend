package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.ResourceType

data class GetAuthorizedResourcesQuery(
    val actorId: ActorId,
    val type: ResourceType,
    val action: ActionType
)
