package com.robotutor.nexora.module.iam.application.command

import com.robotutor.nexora.module.iam.domain.vo.Resource
import com.robotutor.nexora.shared.domain.vo.principal.ActorData

data class AuthorizeResourceCommand(
    val actorData: ActorData,
    val resource: Resource,
)
