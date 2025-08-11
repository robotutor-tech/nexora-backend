package com.robotutor.nexora.modules.auth.repositories

import com.robotutor.nexora.modules.auth.models.DeviceInvitation
import com.robotutor.nexora.modules.auth.models.InvitationId
import com.robotutor.nexora.modules.auth.models.InvitationStatus
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.models.UserId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface DeviceInvitationRepository : ReactiveMongoRepository<DeviceInvitation, InvitationId> {
    fun findAllByPremisesIdAndInvitedByAndStatus(
        premisesId: PremisesId,
        invitedBy: UserId,
        status: InvitationStatus = InvitationStatus.INVITED
    ): Flux<DeviceInvitation>

    fun findByInvitationIdAndStatus(
        invitationId: InvitationId,
        status: InvitationStatus = InvitationStatus.INVITED
    ): Mono<DeviceInvitation>
}
