package com.robotutor.nexora.common.cache.service

import com.robotutor.nexora.shared.domain.Aggregate
import com.robotutor.nexora.shared.domain.specification.Specification

interface SpecificationKeyGenerator<A : Aggregate, R> {
    fun generate(specification: Specification<A>): R
}