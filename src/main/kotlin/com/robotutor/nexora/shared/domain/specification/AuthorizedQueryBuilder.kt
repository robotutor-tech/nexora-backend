package com.robotutor.nexora.shared.domain.specification

import com.robotutor.nexora.shared.application.annotation.ResourceSelector
import com.robotutor.nexora.shared.domain.AggregateId
import com.robotutor.nexora.shared.domain.vo.Identifier
import com.robotutor.nexora.shared.domain.vo.Resources
import org.springframework.stereotype.Component

@Component
class AuthorizedQueryBuilder<T : Identifier, A : AggregateId<A, T>> {
    fun build(resources: Resources<T>): Specification<A> {
        return when (resources.resourceSelector) {
            ResourceSelector.ALL -> IdNotInSpecification(resources.deniedIds)

            ResourceSelector.SPECIFIC -> IdInSpecification<T, A>(resources.allowedIds)
                .and(IdNotInSpecification(resources.deniedIds))
        }
    }
}