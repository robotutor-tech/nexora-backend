package com.robotutor.nexora.modules.iam.domain.entity

import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.DomainEvent
import com.robotutor.nexora.shared.domain.model.*
import java.time.Instant

data class Actor(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val principalType: ActorPrincipalType,
    val principal: ActorPrincipalContext,
    val roleIds: List<RoleId>,
    val state: ActorState = ActorState.ACTIVE,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val version: Long = 0
) : AggregateRoot<Actor, ActorId, DomainEvent>(actorId) {
    companion object {
        fun create(
            actorId: ActorId,
            premisesId: PremisesId,
            principalType: ActorPrincipalType,
            principal: ActorPrincipalContext,
            roleIds: List<RoleId>
        ): Actor {
            return Actor(
                actorId = actorId,
                premisesId = premisesId,
                principalType = principalType,
                principal = principal,
                roleIds = roleIds
            )
        }
    }
}

enum class ActorState {
    ACTIVE,
    INACTIVE
}