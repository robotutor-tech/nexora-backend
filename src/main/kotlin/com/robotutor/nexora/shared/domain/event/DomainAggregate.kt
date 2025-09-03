package com.robotutor.nexora.shared.domain.event

interface DomainModel

open class DomainAggregate<T : DomainEvent>(
    private val domainEvents: MutableList<T> = mutableListOf()
) : DomainModel {

    fun addDomainEvent(event: T): DomainAggregate<T> {
        domainEvents.add(event)
        return this
    }

    fun getDomainEvents(): List<T> {
        return domainEvents.toList()
    }

    fun clearDomainEvents(): DomainAggregate<T> {
        domainEvents.clear()
        return this
    }
}