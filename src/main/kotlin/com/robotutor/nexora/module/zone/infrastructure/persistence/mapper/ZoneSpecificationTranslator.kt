package com.robotutor.nexora.module.zone.infrastructure.persistence.mapper

import com.robotutor.nexora.module.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.module.zone.domain.specification.ZoneByPremisesSpecification
import com.robotutor.nexora.module.zone.domain.specification.ZoneSpecification
import com.robotutor.nexora.common.persistence.mapper.BaseSpecificationTranslator
import org.springframework.data.mongodb.core.query.Criteria

object ZoneSpecificationTranslator : BaseSpecificationTranslator<ZoneAggregate, ZoneSpecification>("zoneId") {
    override fun translateLeaf(specification: ZoneSpecification): Criteria {
        return when (specification) {
            is ZoneByPremisesSpecification -> Criteria.where("premisesId").`is`(specification.premisesId.value)
        }
    }
}