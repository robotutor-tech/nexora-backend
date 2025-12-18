package com.robotutor.nexora.shared.domain.specification

import com.robotutor.nexora.shared.domain.AggregateId
import com.robotutor.nexora.shared.domain.vo.Identifier

class IdInSpecification<T : Identifier, A : AggregateId<A, T>>(
    val allowed: Set<T>
) : Specification<A> {
    override fun isSatisfiedBy(candidate: A): Boolean {
        return candidate.id in allowed
    }
}