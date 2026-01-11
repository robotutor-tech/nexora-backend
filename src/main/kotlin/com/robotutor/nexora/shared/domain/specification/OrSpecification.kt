package com.robotutor.nexora.shared.domain.specification

class OrSpecification<T>(
    val specifications: List<Specification<T>>,
) : Specification<T> {

    override fun isSatisfiedBy(candidate: T): Boolean {
        return specifications.any { isSatisfiedBy(candidate) }
    }
}
