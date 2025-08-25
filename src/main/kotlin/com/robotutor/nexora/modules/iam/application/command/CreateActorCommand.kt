package com.robotutor.nexora.modules.iam.application.command

import com.robotutor.nexora.shared.domain.model.ActorPrincipalContext
import com.robotutor.nexora.shared.domain.model.ActorPrincipalType
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.RoleId

data class CreateActorCommand(
    val premisesId: PremisesId,
    val roles: List<RoleId>,
    val principalType: ActorPrincipalType,
    val principal: ActorPrincipalContext
)