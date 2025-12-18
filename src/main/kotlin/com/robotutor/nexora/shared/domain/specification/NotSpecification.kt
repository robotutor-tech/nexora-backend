package com.robotutor.nexora.shared.domain.specification

class NotSpecification<T>(val spec: Specification<T>) : Specification<T> {
    override fun isSatisfiedBy(candidate: T): Boolean {
        return !spec.isSatisfiedBy(candidate)
    }
}