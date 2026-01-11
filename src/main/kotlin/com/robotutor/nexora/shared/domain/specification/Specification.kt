package com.robotutor.nexora.shared.domain.specification

interface Specification<T> {
    fun isSatisfiedBy(candidate: T): Boolean

    fun and(vararg other: Specification<T>): Specification<T> {
        return AndSpecification(listOf(this, *other))
    }

    fun or(vararg other: Specification<T>): Specification<T> {
        return OrSpecification(listOf(this, *other))
    }

    fun not(): Specification<T> {
        return NotSpecification(this)
    }
}
