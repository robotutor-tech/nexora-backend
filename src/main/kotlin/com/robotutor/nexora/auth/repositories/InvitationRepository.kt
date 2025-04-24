package com.robotutor.nexora.auth.repositories

import com.robotutor.nexora.auth.models.Invitation
import com.robotutor.nexora.auth.models.InvitationId
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.UserId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface InvitationRepository : ReactiveMongoRepository<Invitation, InvitationId> {
    fun findAllByPremisesIdAndCreatedBy(premisesId: PremisesId, userId: UserId): Flux<Invitation>
}
