package com.robotutor.nexora.common.security.application.ports

import com.robotutor.nexora.shared.domain.model.*
import reactor.core.publisher.Mono

data class ActorResponse(
    val actorId: ActorId,
    val role: Role,
    val premisesId: PremisesId,
    val principalType: ActorPrincipalType,
    val principal: ActorPrincipalContext,
)

interface ActorDataRetriever {
    fun getActorData(actorId: ActorId, roleId: RoleId): Mono<ActorResponse>
}