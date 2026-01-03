package com.robotutor.nexora.shared.domain.specification

import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.domain.vo.Identifier

class IdInSpecification<A : AggregateRoot<A, out Identifier, out Event>>(
    val allowed: Set<Identifier>
) : Specification<A> {
    override fun isSatisfiedBy(candidate: A): Boolean {
        return candidate.id in allowed
    }
}