package com.robotutor.nexora.modules.auth.domain.repository

import com.robotutor.nexora.modules.auth.domain.entity.Invitation
import com.robotutor.nexora.modules.auth.domain.entity.InvitationStatus
import com.robotutor.nexora.shared.domain.model.InvitationId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface InvitationRepository {
    fun save(invitation: Invitation): Mono<Invitation>
    fun findAllByInvitationIdInAndStatus(invitationIds: List<InvitationId>, status: InvitationStatus): Flux<Invitation>
    fun findByInvitationIdAndStatus(invitationId: InvitationId, status: InvitationStatus): Mono<Invitation>
}