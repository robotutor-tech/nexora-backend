package com.robotutor.nexora.shared.infrastructure.persistence.mapper

import com.robotutor.nexora.shared.domain.specification.AndSpecification
import com.robotutor.nexora.shared.domain.specification.NotSpecification
import com.robotutor.nexora.shared.domain.specification.OrSpecification
import com.robotutor.nexora.shared.domain.specification.Specification
import org.springframework.data.mongodb.core.query.Criteria

abstract class BaseMongoSpecificationTranslator<D>
    : SpecificationTranslator<D, Criteria> {

    final override fun translate(specification: Specification<D>): Criteria =
        when (specification) {

            is AndSpecification ->
                Criteria().andOperator(translate(specification.left), translate(specification.right))

            is OrSpecification ->
                Criteria().orOperator(translate(specification.left), translate(specification.right))

            is NotSpecification ->
                Criteria().norOperator(translate(specification.spec))

            else -> translateLeaf(specification)
        }

    protected abstract fun translateLeaf(specification: Specification<D>): Criteria
}
