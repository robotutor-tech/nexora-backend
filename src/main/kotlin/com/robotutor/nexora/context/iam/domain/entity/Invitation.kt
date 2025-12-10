package com.robotutor.nexora.context.iam.domain.entity

import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.context.iam.domain.event.InvitationAcceptedEvent
import com.robotutor.nexora.context.iam.domain.vo.SessionId
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.model.*
import com.robotutor.nexora.shared.domain.vo.Name
import java.time.Instant

data class Invitation(
    val invitationId: InvitationId,
    val premisesId: PremisesId,
    val name: Name,
    val zoneId: ZoneId,
    val invitedBy: ActorId,
    val sessionId: SessionId,
    val createdAt: Instant = Instant.now(),
    var status: InvitationStatus = InvitationStatus.INVITED,
    val version: Long = 0
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
            sessionId: SessionId
        ): Invitation {
            return Invitation(
                invitationId = invitationId,
                premisesId = premisesId,
                name = name,
                zoneId = zoneId,
                invitedBy = invitedBy,
                sessionId = sessionId,
            )
        }
    }
}


enum class InvitationStatus {
    INVITED,
    ACCEPTED,
}

