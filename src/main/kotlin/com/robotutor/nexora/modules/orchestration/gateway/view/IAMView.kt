package com.robotutor.nexora.modules.orchestration.gateway.view

import com.robotutor.nexora.modules.iam.controllers.view.RoleIdWithName
import com.robotutor.nexora.modules.iam.models.ActorState
import com.robotutor.nexora.modules.iam.models.RoleId
import com.robotutor.nexora.modules.iam.models.RoleType
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.models.ActorId
import com.robotutor.nexora.shared.domain.model.ActorIdentifier
import com.robotutor.nexora.shared.domain.model.Identifier

data class PremisesActorView(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val identifier: Identifier<ActorIdentifier>,
    val roles: List<RoleIdWithName>,
    val state: ActorState,
)

data class RoleView(
    val roleId: RoleId,
    val premisesId: PremisesId,
    val name: String,
    val role: RoleType,
)
