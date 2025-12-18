package com.robotutor.nexora.shared.domain.specification

import com.robotutor.nexora.shared.domain.AggregateId
import com.robotutor.nexora.shared.domain.vo.Identifier

class IdNotInSpecification<T : Identifier, A : AggregateId<A, T>>(
     val denied: Set<T>
) : Specification<A> {

    override fun isSatisfiedBy(candidate: A): Boolean {
        return candidate.id !in denied
    }
}