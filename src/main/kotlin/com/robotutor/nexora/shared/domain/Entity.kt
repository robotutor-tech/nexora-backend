package com.robotutor.nexora.shared.domain

import com.robotutor.nexora.shared.domain.vo.Identifier

abstract class Entity<T, ID: Identifier>(open val id: ID) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Entity<*, *>
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "${this::class.simpleName}(id=$id)"
    }
}