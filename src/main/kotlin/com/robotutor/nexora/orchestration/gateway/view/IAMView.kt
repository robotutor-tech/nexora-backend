package com.robotutor.nexora.orchestration.gateway.view

import com.robotutor.nexora.iam.models.ActorState
import com.robotutor.nexora.iam.models.PolicyId
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.iam.models.RoleType
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.ActorIdentifier
import com.robotutor.nexora.security.models.Identifier

data class PremisesActorView(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val identifier: Identifier<ActorIdentifier>,
    val role: RoleView,
    val state: ActorState,
)

data class RoleView(
    val roleId: RoleId,
    val premisesId: PremisesId,
    val name: String,
    val role: RoleType,
)

data class PolicyView(
    val policyId: PolicyId,
    val premisesId: PremisesId,
    val name: String,
)