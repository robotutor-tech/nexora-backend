package com.robotutor.nexora.shared.domain

interface Aggregate

abstract class AggregateRoot<T, ID, E : DomainEvent>(id: ID) : Entity<T, ID>(id), Aggregate {
    private val _domainEvents = mutableListOf<E>()
    val domainEvents: List<E> get() = _domainEvents

    protected fun addEvent(event: E) {
        _domainEvents += event
    }

    fun clearEvents() {
        _domainEvents.clear()
    }
}