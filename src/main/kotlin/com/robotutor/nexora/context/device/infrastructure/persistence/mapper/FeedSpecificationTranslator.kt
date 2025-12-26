package com.robotutor.nexora.context.device.infrastructure.persistence.mapper

import com.robotutor.nexora.context.device.domain.aggregate.FeedAggregate
import com.robotutor.nexora.context.device.domain.specification.FeedByPremisesIdSpecification
import com.robotutor.nexora.context.device.domain.specification.FeedSpecification
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.BaseSpecificationTranslator
import org.springframework.data.mongodb.core.query.Criteria

object FeedSpecificationTranslator : BaseSpecificationTranslator<FeedAggregate, FeedSpecification>("feedId") {
    override fun translateLeaf(specification: FeedSpecification): Criteria {
        return when (specification) {
            is FeedByPremisesIdSpecification -> Criteria.where("premisesId").`is`(specification.premisesId.value)
        }
    }
}