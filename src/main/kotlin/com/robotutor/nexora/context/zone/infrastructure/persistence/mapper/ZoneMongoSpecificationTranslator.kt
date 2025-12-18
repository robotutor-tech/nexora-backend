package com.robotutor.nexora.context.zone.infrastructure.persistence.mapper

import com.robotutor.nexora.context.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.context.zone.domain.specification.ZoneByPremisesSpecification
import com.robotutor.nexora.shared.domain.vo.ZoneId
import com.robotutor.nexora.shared.domain.specification.IdInSpecification
import com.robotutor.nexora.shared.domain.specification.IdNotInSpecification
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.BaseMongoSpecificationTranslator
import org.springframework.data.mongodb.core.query.Criteria

object ZoneMongoSpecificationTranslator : BaseMongoSpecificationTranslator<ZoneAggregate>() {
    override fun translateLeaf(specification: Specification<ZoneAggregate>): Criteria {
        return when (specification) {
            is ZoneByPremisesSpecification -> Criteria.where("premisesId").`is`(specification.premisesId.value)
            is IdInSpecification<*, *> -> Criteria.where("zoneId")
                .`in`(specification.allowed.map { (it as ZoneId).value })

            is IdNotInSpecification<*, *> -> Criteria.where("zoneId")
                .nin(specification.denied.map { (it as ZoneId).value })

            else -> throw IllegalStateException("Unsupported specification: $specification for ZoneAggregate")
        }
    }
}