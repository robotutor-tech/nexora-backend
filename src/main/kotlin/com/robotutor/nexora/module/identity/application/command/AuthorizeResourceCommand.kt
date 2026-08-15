package com.robotutor.nexora.module.identity.application.command

import com.robotutor.nexora.module.identity.domain.vo.Resource
import com.robotutor.nexora.shared.domain.vo.ActorData

data class AuthorizeResourceCommand(
    val actorData: ActorData,
    val resource: Resource,
)
