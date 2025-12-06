package com.robotutor.nexora.context.iam.domain.entity

import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.context.iam.domain.event.InvitationAcceptedEvent
import com.robotutor.nexora.context.iam.domain.vo.TokenId
import com.robotutor.nexora.shared.domain.AggregateRoot
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
) : AggregateRoot<Invitation, InvitationId, IAMEvent>(invitationId) {
    fun markAsAccepted(): Invitation {
        this.status = InvitationStatus.ACCEPTED
        this.addEvent(InvitationAcceptedEvent(invitationId))
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

