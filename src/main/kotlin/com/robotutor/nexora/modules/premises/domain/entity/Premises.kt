package com.robotutor.nexora.modules.premises.domain.entity

import com.robotutor.nexora.modules.premises.domain.event.PremisesEvent
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.UserId
import java.time.Instant

data class Premises(
    val premisesId: PremisesId,
    val name: Name,
    val address: Address,
    val owner: UserId,
    val createdAt: Instant = Instant.now(),
    val version: Long? = null
) : AggregateRoot<Premises, PremisesId, PremisesEvent>(premisesId) {
    companion object {
        fun register(premisesId: PremisesId, name: Name, address: Address, owner: UserId): Premises {
            //TODO: add domain event
            return Premises(premisesId = premisesId, name = name, address = address, owner = owner)
        }
    }
}
