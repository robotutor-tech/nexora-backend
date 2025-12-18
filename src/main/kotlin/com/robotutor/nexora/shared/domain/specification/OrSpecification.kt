package com.robotutor.nexora.shared.domain.specification

class OrSpecification<T>(
    val left: Specification<T>,
    val right: Specification<T>
) : Specification<T> {

    override fun isSatisfiedBy(candidate: T): Boolean {
        return left.isSatisfiedBy(candidate) || right.isSatisfiedBy(candidate)
    }
}
