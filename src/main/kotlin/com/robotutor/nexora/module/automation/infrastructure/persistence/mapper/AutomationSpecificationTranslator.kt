package com.robotutor.nexora.module.automation.infrastructure.persistence.mapper

import com.robotutor.nexora.common.persistence.mapper.BaseSpecificationTranslator
import com.robotutor.nexora.module.automation.domain.aggregate.AutomationAggregate
import com.robotutor.nexora.module.automation.domain.specification.AutomationByPremisesIdSpecification
import com.robotutor.nexora.module.automation.domain.specification.AutomationSpecification
import org.springframework.data.mongodb.core.query.Criteria

object AutomationSpecificationTranslator :
    BaseSpecificationTranslator<AutomationAggregate, AutomationSpecification>("automationId") {
    override fun translateLeaf(specification: AutomationSpecification): Criteria {
        return when (specification) {
            is AutomationByPremisesIdSpecification -> Criteria.where("premisesId").`is`(specification.premisesId.value)
        }
    }
}

