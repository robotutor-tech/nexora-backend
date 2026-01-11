package com.robotutor.nexora.shared.domain.specification

class AndSpecification<T>(
    val specifications: List<Specification<T>>,
) : Specification<T> {
    override fun isSatisfiedBy(candidate: T): Boolean {
        return specifications.all { isSatisfiedBy(candidate) }
    }
}