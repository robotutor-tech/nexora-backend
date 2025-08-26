package com.robotutor.nexora.shared.domain.event

interface DomainModel

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