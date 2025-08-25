package com.robotutor.nexora.modules.auth.domain.repository

import com.robotutor.nexora.modules.auth.domain.model.Invitation
import com.robotutor.nexora.shared.domain.model.InvitationId
import reactor.core.publisher.Mono

interface InvitationRepository {
    fun save(invitation: Invitation): Mono<Invitation>
    fun findByInvitationId(invitationId: InvitationId): Mono<Invitation>
}