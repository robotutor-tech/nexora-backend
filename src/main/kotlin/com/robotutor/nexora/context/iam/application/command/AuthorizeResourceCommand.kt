package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.context.iam.domain.vo.Resource
import com.robotutor.nexora.shared.domain.vo.principal.ActorData

data class AuthorizeResourceCommand(
    val actorData: ActorData,
    val resource: Resource,
)
