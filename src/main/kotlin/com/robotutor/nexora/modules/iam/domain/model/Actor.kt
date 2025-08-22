package com.robotutor.nexora.modules.iam.domain.model

import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.ActorPrincipalType
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import com.robotutor.nexora.shared.domain.model.RoleId
import java.time.Instant

data class Actor(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val principalType: ActorPrincipalType,
    val principal: PrincipalContext,
    val roleIds: List<RoleId>,
    val state: ActorState = ActorState.ACTIVE,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val version: Long? = null
)

enum class ActorState {
    ACTIVE,
    INACTIVE
}