package com.robotutor.nexora.shared.domain.specification

class NotSpecification<T>(val specification: Specification<T>) : Specification<T> {
    override fun isSatisfiedBy(candidate: T): Boolean {
        return !specification.isSatisfiedBy(candidate)
    }
}