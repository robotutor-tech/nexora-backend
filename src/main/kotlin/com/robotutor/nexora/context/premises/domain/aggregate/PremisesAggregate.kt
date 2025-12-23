package com.robotutor.nexora.context.premises.domain.aggregate

import com.robotutor.nexora.context.premises.domain.event.PremisesEvent
import com.robotutor.nexora.context.premises.domain.event.PremisesRegisteredEvent
import com.robotutor.nexora.context.premises.domain.exceptions.PremisesError
import com.robotutor.nexora.context.premises.domain.vo.Address
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.exception.InvalidStateException
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import java.time.Instant

class PremisesAggregate private constructor(
    val premisesId: PremisesId,
    val ownerId: AccountId,
    val createdAt: Instant,
    val name: Name,
    val address: Address,
    private var state: PremisesState,
    private var updatedAt: Instant,
) : AggregateRoot<PremisesAggregate, PremisesId, PremisesEvent>(premisesId) {

    fun getState(): PremisesState = state
    fun getUpdatedAt(): Instant = updatedAt


    companion object {
        fun register(
            premisesId: PremisesId,
            name: Name,
            address: Address,
            ownerId: AccountId,
        ): PremisesAggregate {
            val premises = create(premisesId = premisesId, name = name, address = address, ownerId = ownerId)
            premises.addEvent(
                PremisesRegisteredEvent(premises.premisesId, premises.name, premises.ownerId)
            )
            return premises
        }

        fun create(
            premisesId: PremisesId,
            name: Name,
            address: Address,
            ownerId: AccountId,
            state: PremisesState = PremisesState.REGISTERED,
            createdAt: Instant = Instant.now(),
            updatedAt: Instant = Instant.now(),
        ): PremisesAggregate {
            return PremisesAggregate(
                premisesId = premisesId,
                ownerId = ownerId,
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