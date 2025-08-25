package com.robotutor.nexora.shared.domain.event

import com.robotutor.nexora.shared.domain.model.DomainModel

open class DomainAggregate(
    private val domainEvents: MutableList<DomainEvent> = mutableListOf()
) : DomainModel {

    fun addDomainEvent(event: DomainEvent): DomainAggregate {
        domainEvents.add(event)
        return this
    }

    fun getDomainEvents(): List<DomainEvent> {
        return domainEvents.toList()
    }

    fun clearDomainEvents(): DomainAggregate {
        domainEvents.clear()
        return this
    }
}