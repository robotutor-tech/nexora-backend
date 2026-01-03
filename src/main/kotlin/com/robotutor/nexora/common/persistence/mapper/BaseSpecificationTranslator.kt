package com.robotutor.nexora.common.persistence.mapper

import com.robotutor.nexora.shared.domain.Aggregate
import com.robotutor.nexora.shared.domain.specification.AndSpecification
import com.robotutor.nexora.shared.domain.specification.IdInSpecification
import com.robotutor.nexora.shared.domain.specification.IdNotInSpecification
import com.robotutor.nexora.shared.domain.specification.NotSpecification
import com.robotutor.nexora.shared.domain.specification.OrSpecification
import com.robotutor.nexora.shared.domain.specification.Specification
import org.springframework.data.mongodb.core.query.Criteria

abstract class BaseSpecificationTranslator<A : Aggregate, S : Specification<A>>(val identifierKey: String) :
    SpecificationTranslator<A, Criteria> {

    @Suppress("UNCHECKED_CAST")
    final override fun translate(specification: Specification<A>): Criteria {
        return when (specification) {
            is AndSpecification ->
                Criteria().andOperator(translate(specification.left), translate(specification.right))

            is OrSpecification ->
                Criteria().orOperator(translate(specification.left), translate(specification.right))

            is NotSpecification ->
                Criteria().norOperator(translate(specification.spec))

            is IdInSpecification -> if (specification.allowed.isEmpty()) Criteria()
            else Criteria.where(identifierKey).`in`(specification.allowed.map { it.value })

            is IdNotInSpecification -> if (specification.denied.isEmpty()) Criteria()
            else Criteria.where(identifierKey).nin(specification.denied.map { it.value })

            else -> translateLeaf(specification as S)
        }
    }

    protected abstract fun translateLeaf(specification: S): Criteria
}
