package com.robotutor.nexora.context.premises.domain.aggregate

import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.context.premises.domain.event.PremisesDomainEvent
import com.robotutor.nexora.context.premises.domain.event.PremisesRegisteredEvent
import com.robotutor.nexora.context.premises.domain.exceptions.PremisesError
import com.robotutor.nexora.context.premises.domain.vo.Address
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.exception.InvalidStateException
import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.AccountType
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import java.time.Instant

class PremisesAggregate private constructor(
    val premisesId: PremisesId,
    val registeredBy: AccountData,
    val createdAt: Instant,
    val name: Name,
    val address: Address,
    private var state: PremisesState,
    private var updatedAt: Instant,
) : AggregateRoot<PremisesAggregate, PremisesId, PremisesDomainEvent>(premisesId) {

    fun getState(): PremisesState = state
    fun getUpdatedAt(): Instant = updatedAt

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
            val premises = create(premisesId = premisesId, name = name, address = address, registeredBy = registeredBy)
            premises.addEvent(
                PremisesRegisteredEvent(premises.premisesId, premises.name, premises.registeredBy)
            )
            return premises
        }

        fun create(
            premisesId: PremisesId,
            name: Name,
            address: Address,
            registeredBy: AccountData,
            state: PremisesState = PremisesState.REGISTERED,
            createdAt: Instant = Instant.now(),
            updatedAt: Instant = Instant.now(),
        ): PremisesAggregate {
            return PremisesAggregate(
                premisesId = premisesId,
                registeredBy = registeredBy,
                createdAt = createdAt,
                name = name,
                address = address,
                state = state,
                updatedAt = updatedAt
            )
        }
    }

    fun activate(): PremisesAggregate {
        if (state != PremisesState.REGISTERED) {
            throw InvalidStateException(PremisesError.NEXORA0502)
        }
        state = PremisesState.ACTIVE
        updatedAt = Instant.now()
        return this
    }
}

enum class PremisesState {
    REGISTERED,
    ACTIVE,
    INACTIVE
}