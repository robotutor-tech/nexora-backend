package com.robotutor.nexora.modules.auth.domain.model

import com.robotutor.nexora.shared.domain.event.DomainAggregate
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.InvitationId
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ZoneId
import com.robotutor.nexora.shared.domain.model.Name
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
) : DomainAggregate() {
    fun markAsAccepted(): Invitation {
        this.status = InvitationStatus.ACCEPTED
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

