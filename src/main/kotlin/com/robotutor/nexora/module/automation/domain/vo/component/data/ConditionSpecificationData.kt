package com.robotutor.nexora.module.automation.domain.vo.component.data

import com.robotutor.nexora.module.automation.domain.vo.component.Condition
import com.robotutor.nexora.shared.domain.specification.Specification

data class ConditionSpecificationData<C : Condition, D : ComponentData<C>>(
    private val condition: D
) : Specification<D> {
    override fun isSatisfiedBy(candidate: D): Boolean {
        return candidate == condition
    }
}