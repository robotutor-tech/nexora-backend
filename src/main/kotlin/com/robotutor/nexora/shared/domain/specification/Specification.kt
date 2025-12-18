package com.robotutor.nexora.shared.domain.specification

interface Specification<T> {
    fun isSatisfiedBy(candidate: T): Boolean

    fun and(other: Specification<T>): Specification<T> {
        return AndSpecification(this, other)
    }

    fun or(other: Specification<T>): Specification<T> {
        return OrSpecification(this, other)
    }

    fun not(): Specification<T> {
        return NotSpecification(this)
    }
}
