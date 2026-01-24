package com.robotutor.nexora.module.automation.domain.specification

import com.robotutor.nexora.module.automation.domain.aggregate.AutomationAggregate
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class AutomationByPremisesIdSpecification(val premisesId: PremisesId) : AutomationSpecification {
    override fun isSatisfiedBy(candidate: AutomationAggregate): Boolean {
        return candidate.premisesId == premisesId
    }
}
