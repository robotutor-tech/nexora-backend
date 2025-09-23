package com.robotutor.nexora.modules.auth.domain.entity

import com.robotutor.nexora.modules.auth.domain.event.AuthEvent
import com.robotutor.nexora.modules.auth.domain.event.InvitationAcceptedEvent
import com.robotutor.nexora.shared.domain.event.DomainAggregate
import com.robotutor.nexora.shared.domain.model.*
import java.time.Instant

data class Invitation(
    val invitationId: InvitationId,
    val premisesId: PremisesId,
    val name: Name,
    val zoneId: ZoneId,
    val invitedBy: ActorId,
    val tokenId: TokenId,
    val createdAt: Instant = Instant.now(),
    var status: InvitationStatus = InvitationStatus.INVITED,
    val version: Long? = null
) : DomainAggregate<AuthEvent>() {
    fun markAsAccepted(): Invitation {
        this.status = InvitationStatus.ACCEPTED
        this.addDomainEvent(InvitationAcceptedEvent(invitationId))
        return this
    }

    companion object {
        fun create(
            invitationId: InvitationId,
            premisesId: PremisesId,
            name: Name,
            zoneId: ZoneId,
            invitedBy: ActorId,
            tokenId: TokenId
        ): Invitation {
            return Invitation(
                invitationId = invitationId,
                premisesId = premisesId,
                name = name,
                zoneId = zoneId,
                invitedBy = invitedBy,
                tokenId = tokenId,
            )
        }
    }
}


enum class InvitationStatus {
    INVITED,
    ACCEPTED,
}

