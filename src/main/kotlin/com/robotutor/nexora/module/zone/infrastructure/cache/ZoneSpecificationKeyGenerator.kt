package com.robotutor.nexora.module.zone.infrastructure.cache

import com.robotutor.nexora.common.cache.service.BaseSpecificationKeyGenerator
import com.robotutor.nexora.common.cache.service.KeyGenerator
import com.robotutor.nexora.common.cache.service.argumentIndexForSpecification
import com.robotutor.nexora.module.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.module.zone.domain.specification.ZoneByPremisesSpecification
import com.robotutor.nexora.module.zone.domain.specification.ZoneSpecification
import com.robotutor.nexora.shared.domain.specification.Specification
import java.lang.reflect.Method

class ZoneSpecificationKeyGenerator : KeyGenerator,
    BaseSpecificationKeyGenerator<ZoneAggregate, ZoneSpecification>("zoneId") {
    override fun generate(method: Method, vararg args: Any): String {
        val index = argumentIndexForSpecification(method, ZoneAggregate::class.java)

        @Suppress("UNCHECKED_CAST")
        val specification = args[index] as Specification<ZoneAggregate>
        return this.generate(specification)
    }

    override fun generateLeaf(specification: ZoneSpecification): String {
        return when (specification) {
            is ZoneByPremisesSpecification -> "{premisesId: ${specification.premisesId.value}}"
        }
    }
}