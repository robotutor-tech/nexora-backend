package com.robotutor.nexora.shared.domain.specification

import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.domain.vo.Identifier
import com.robotutor.nexora.shared.domain.vo.ResourceSelector
import com.robotutor.nexora.shared.domain.vo.Resources
import org.springframework.stereotype.Component

@Component
class ResourceSpecificationBuilder<A : AggregateRoot<A, out Identifier, out Event>> {
    fun build(resources: Resources): Specification<A> {
        return when (resources.resourceSelector) {
            ResourceSelector.ALL -> IdNotInSpecification(resources.deniedIds)

            ResourceSelector.SPECIFIC -> IdNotInSpecification<A>(resources.deniedIds)
                .and(IdInSpecification(resources.allowedIds))
        }
    }
}