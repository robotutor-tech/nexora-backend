package com.robotutor.nexora.common.cache.service

import com.robotutor.nexora.shared.domain.Aggregate
import com.robotutor.nexora.shared.domain.specification.*

abstract class BaseSpecificationKeyGenerator<A : Aggregate, S : Specification<A>>(
    val identifierKey: String
) : SpecificationKeyGenerator<A, String> {


    final override fun generate(specification: Specification<A>): String {
        return when (specification) {
            is AndSpecification -> {
                "{operator:AND, specifications:${specification.specifications.map { generate(it) }}}"
            }

            is OrSpecification ->
                "{operator:OR, specifications:${specification.specifications.map { generate(it) }}}"

            is NotSpecification -> "{operator:NOT, spec:${generate(specification.specification)}}"

            is IdInSpecification -> if (specification.allowed.isEmpty()) "{$identifierKey: {allowed: []}}"
            else "{$identifierKey:{allowed: ${specification.allowed.map { it.value }}}}"

            is IdNotInSpecification -> if (specification.denied.isEmpty()) "{$identifierKey: {denied: []}}"
            else "{$identifierKey:{denied: ${specification.denied.map { it.value }}}}"

            else -> generateLeaf(specification as S)
        }
    }

    protected abstract fun generateLeaf(specification: S): String
}
