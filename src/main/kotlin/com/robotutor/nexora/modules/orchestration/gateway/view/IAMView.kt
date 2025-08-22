package com.robotutor.nexora.modules.orchestration.gateway.view

import com.robotutor.nexora.common.security.models.ActorId
import com.robotutor.nexora.common.security.models.PremisesId
import com.robotutor.nexora.modules.iam.domain.model.ActorState
import com.robotutor.nexora.shared.domain.model.ActorPrincipalType
import com.robotutor.nexora.shared.domain.model.Identifier

data class PremisesActorView(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val identifier: Identifier<ActorPrincipalType>,
//    val roles: List<RoleIdWithName>,
    val state: ActorState,
)

//data class RoleView(
//    val roleId: RoleId,
//    val premisesId: PremisesId,
//    val name: String,
//    val role: RoleType,
//)
