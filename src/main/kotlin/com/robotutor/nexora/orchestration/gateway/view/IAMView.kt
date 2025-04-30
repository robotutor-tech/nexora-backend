package com.robotutor.nexora.orchestration.gateway.view

import com.robotutor.nexora.iam.models.ActorId
import com.robotutor.nexora.iam.models.ActorState
import com.robotutor.nexora.iam.models.ActorType
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.iam.models.RoleType
import com.robotutor.nexora.premises.models.PremisesId

data class PremisesActorView(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val type: ActorType,
    val identifier: String,
    val role: RoleView,
    val state: ActorState,
)

data class RoleView(
    val roleId: RoleId,
    val premisesId: PremisesId,
    val name: String,
    val role: RoleType,
)