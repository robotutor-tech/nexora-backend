package com.robotutor.nexora.modules.auth.adapters.persistance.repository

import com.robotutor.nexora.modules.auth.adapters.persistance.model.InvitationDocument
import com.robotutor.nexora.modules.auth.domain.model.Invitation
import com.robotutor.nexora.modules.auth.domain.repository.InvitationRepository
import com.robotutor.nexora.shared.domain.model.InvitationId
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class MongoInvitationRepository(
    private val invitationRepository: InvitationDocumentRepository,
) : InvitationRepository {

    override fun save(invitation: Invitation): Mono<Invitation> {
        return invitationRepository.save(InvitationDocument.from(invitation))
            .map { it.toDomainModel() }
    }

    override fun findByInvitationId(invitationId: InvitationId): Mono<Invitation> {
        return invitationRepository.findByInvitationId(invitationId.value)
            .map { it.toDomainModel() }
    }
}