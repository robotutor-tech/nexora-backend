package com.robotutor.nexora.modules.auth.adapters.persistance.repository

import com.robotutor.nexora.modules.auth.adapters.persistance.model.InvitationDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface InvitationDocumentRepository : ReactiveCrudRepository<InvitationDocument, String> {
    fun findByInvitationId(invitationId: String): Mono<InvitationDocument>
}