package com.robotutor.nexora.common.persistence.mongo.mapper

import com.robotutor.nexora.shared.domain.Aggregate
import com.robotutor.nexora.shared.domain.specification.Specification

interface SpecificationTranslator<A : Aggregate, Q> {
    fun translate(specification: Specification<A>): Q
}