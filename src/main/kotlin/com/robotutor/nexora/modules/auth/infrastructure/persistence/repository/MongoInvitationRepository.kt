package com.robotutor.nexora.modules.auth.infrastructure.persistence.repository

import com.robotutor.nexora.modules.auth.domain.entity.Invitation
import com.robotutor.nexora.modules.auth.domain.entity.InvitationStatus
import com.robotutor.nexora.modules.auth.domain.repository.InvitationRepository
import com.robotutor.nexora.modules.auth.infrastructure.persistence.mapper.InvitationDocumentMapper
import com.robotutor.nexora.modules.auth.infrastructure.persistence.document.InvitationDocument
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.InvitationId
import com.robotutor.nexora.shared.infrastructure.persistence.repository.MongoRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class MongoInvitationRepository(
    mongoTemplate: ReactiveMongoTemplate,
) : MongoRepository<Invitation, InvitationDocument>(
    mongoTemplate, InvitationDocument::class.java,
    InvitationDocumentMapper()
), InvitationRepository {
    override fun save(invitation: Invitation): Mono<Invitation> {
        val query = Query(Criteria.where("invitationId").`is`(invitation.invitationId.value))
        return this.findAndReplace(query, invitation)
    }

    override fun findByInvitationIdAndStatus(invitationId: InvitationId, status: InvitationStatus): Mono<Invitation> {
        val query = Query(
            Criteria.where("invitationId").`is`(invitationId.value)
                .and("status").`is`(status)
        )
        return this.findOne(query)
    }

    override fun findAllByInvitedByAndStatus(
        actorId: ActorId,
        status: InvitationStatus
    ): Flux<Invitation> {
        val query = Query(
            Criteria.where("invitedBy").`is`(actorId.value)
                .and("status").`is`(status)
        )
        return this.findAll(query)
    }
}
