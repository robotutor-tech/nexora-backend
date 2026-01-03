package com.robotutor.nexora.shared.domain.specification

import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.domain.vo.Identifier

class IdNotInSpecification<A : AggregateRoot<A, out Identifier, out Event>>(
    val denied: Set<Identifier>
) : Specification<A> {

    override fun isSatisfiedBy(candidate: A): Boolean {
        return candidate.id !in denied
    }
}