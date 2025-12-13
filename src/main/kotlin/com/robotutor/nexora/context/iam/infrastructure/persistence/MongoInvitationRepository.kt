package com.robotutor.nexora.context.iam.infrastructure.persistence

import com.robotutor.nexora.context.iam.domain.entity.Invitation
import com.robotutor.nexora.context.iam.domain.entity.InvitationStatus
import com.robotutor.nexora.context.iam.domain.repository.InvitationRepository
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.InvitationDocument
import com.robotutor.nexora.context.iam.infrastructure.persistence.mapper.InvitationDocumentMapper
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
    InvitationDocumentMapper
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

    override fun findAllByInvitationIdInAndStatus(
        invitationIds: List<InvitationId>,
        status: InvitationStatus
    ): Flux<Invitation> {
        val query = Query(
            Criteria.where("invitationId").`in`(invitationIds.map { it.value })
                .and("status").`is`(status)
        )
        return this.findAll(query)
    }
}
