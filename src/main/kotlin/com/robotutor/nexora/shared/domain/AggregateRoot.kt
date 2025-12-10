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

    // These are persistence-level fields
    private var _objectId: String? = null   // MongoDB _id (String or ObjectId)
    private var _version: Long? = null      // Optimistic lock version

    fun getObjectId(): String? = _objectId
    fun setObjectId(value: String?) {
        _objectId = value
    }

    fun getVersion(): Long? = _version
    fun setVersion(value: Long?) {
        _version = value
    }
}