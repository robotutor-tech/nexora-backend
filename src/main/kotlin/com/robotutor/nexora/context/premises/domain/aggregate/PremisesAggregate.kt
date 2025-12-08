package com.robotutor.nexora.context.premises.domain.aggregate

import com.robotutor.nexora.common.security.domain.vo.AccountData
import com.robotutor.nexora.context.iam.domain.aggregate.AccountType
import com.robotutor.nexora.context.premises.domain.event.PremisesEvent
import com.robotutor.nexora.context.premises.domain.event.PremisesRegisteredEvent
import com.robotutor.nexora.context.premises.domain.vo.Address
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.utility.validation
import java.time.Instant

data class PremisesAggregate(
    val premisesId: PremisesId,
    val name: Name,
    val address: Address,
    val registeredBy: AccountData,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val version: Long? = null
) : AggregateRoot<PremisesAggregate, PremisesId, PremisesEvent>(premisesId) {
    init {
        validation(registeredBy.type == AccountType.HUMAN) { "Only humans can create premises" }
    }

    companion object {
        fun register(
            premisesId: PremisesId,
            name: Name,
            address: Address,
            registeredBy: AccountData
        ): PremisesAggregate {
            val premisesAggregate =
                PremisesAggregate(premisesId = premisesId, name = name, address = address, registeredBy = registeredBy)
            premisesAggregate.addEvent(
                PremisesRegisteredEvent(
                    premisesAggregate.premisesId,
                    premisesAggregate.name,
                    premisesAggregate.registeredBy
                )
            )
            return premisesAggregate
        }
    }
}