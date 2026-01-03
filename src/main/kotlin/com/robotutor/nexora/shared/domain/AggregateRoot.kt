package com.robotutor.nexora.shared.domain

import com.robotutor.nexora.shared.domain.vo.Identifier

interface Aggregate

abstract class AggregateRoot<T : AggregateRoot<T, ID, E>, ID : Identifier, E : Event>(
    id: ID
) : Aggregate, Entity<T, ID>(id) {
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
    fun getVersion(): Long? = _version

    @Suppress("UNCHECKED_CAST")
    fun setObjectIdAndVersion(objectId: String?, version: Long?): T {
        _objectId = objectId
        _version = version
        return this as T
    }
}