package com.robotutor.nexora.module.iam.infrastructure.persistence.mapper

import com.robotutor.nexora.module.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.module.iam.domain.specification.ActorByAccountIdSpecification
import com.robotutor.nexora.module.iam.domain.specification.ActorByPremisesIdSpecification
import com.robotutor.nexora.module.iam.domain.specification.ActorSpecification
import com.robotutor.nexora.common.persistence.mapper.BaseSpecificationTranslator
import org.springframework.data.mongodb.core.query.Criteria

object ActorSpecificationTranslator : BaseSpecificationTranslator<ActorAggregate, ActorSpecification>("deviceId") {
    override fun translateLeaf(specification: ActorSpecification): Criteria {
        return when (specification) {
            is ActorByAccountIdSpecification -> Criteria.where("accountId").`is`(specification.accountId.value)
            is ActorByPremisesIdSpecification -> Criteria.where("premisesId").`is`(specification.premisesId.value)
        }
    }
}