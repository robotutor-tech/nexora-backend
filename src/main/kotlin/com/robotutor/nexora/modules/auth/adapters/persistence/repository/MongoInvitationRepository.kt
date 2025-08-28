package com.robotutor.nexora.modules.auth.adapters.persistence.repository

import com.robotutor.nexora.modules.auth.adapters.persistence.mapper.InvitationDocumentMapper
import com.robotutor.nexora.modules.auth.adapters.persistence.model.InvitationDocument
import com.robotutor.nexora.modules.auth.domain.model.Invitation
import com.robotutor.nexora.modules.auth.domain.repository.InvitationRepository
import com.robotutor.nexora.shared.adapters.persistence.repository.MongoRepository
import com.robotutor.nexora.shared.domain.model.InvitationId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoInvitationRepository(
    mongoTemplate: ReactiveMongoTemplate,
) : MongoRepository<Invitation, InvitationDocument>(mongoTemplate, InvitationDocument::class.java, InvitationDocumentMapper()),
    InvitationRepository {
    override fun save(invitation: Invitation): Mono<Invitation> {
        val query = Query(Criteria.where("invitationId").`is`(invitation.invitationId.value))
        return this.findAndReplace(query, invitation)
    }

    override fun findByInvitationId(invitationId: InvitationId): Mono<Invitation> {
        val query = Query(Criteria.where("invitationId").`is`(invitationId.value))
        return this.findOne(query)
    }
}
