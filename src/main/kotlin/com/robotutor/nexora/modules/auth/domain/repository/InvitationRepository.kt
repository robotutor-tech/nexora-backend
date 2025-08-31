package com.robotutor.nexora.modules.auth.domain.repository

import com.robotutor.nexora.modules.auth.domain.model.Invitation
import com.robotutor.nexora.modules.auth.domain.model.InvitationStatus
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.InvitationId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface InvitationRepository {
    fun save(invitation: Invitation): Mono<Invitation>
    fun findByInvitationId(invitationId: InvitationId): Mono<Invitation>
    fun findAllByInvitedByAndStatus(actorId: ActorId, status: InvitationStatus): Flux<Invitation>
}