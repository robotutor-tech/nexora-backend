package com.robotutor.nexora.modules.premises.domain.model

import com.robotutor.nexora.shared.domain.event.DomainAggregate
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.UserId
import com.robotutor.nexora.shared.domain.model.Name
import java.time.Instant

data class Premises(
    val premisesId: PremisesId,
    val name: Name,
    val owner: UserId,
    val createdAt: Instant = Instant.now(),
    val version: Long? = null
) : DomainAggregate() {
    companion object {
        fun register(premisesId: PremisesId, name: Name, owner: UserId): Premises {
            //TODO: add domain event
            return Premises(premisesId = premisesId, name = name, owner = owner)
        }
    }
}

