package com.robotutor.nexora.shared.infrastructure.persistence.mapper

import com.robotutor.nexora.shared.domain.specification.Specification

interface SpecificationTranslator<D, Q> {
    fun translate(specification: Specification<D>): Q
}