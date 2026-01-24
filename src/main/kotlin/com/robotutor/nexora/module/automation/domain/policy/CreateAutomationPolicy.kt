package com.robotutor.nexora.module.automation.domain.policy

import com.robotutor.nexora.module.automation.domain.entity.ResolvedAutomation
import com.robotutor.nexora.module.automation.domain.vo.component.Component
import com.robotutor.nexora.module.automation.domain.vo.component.Condition
import com.robotutor.nexora.module.automation.domain.vo.component.data.AutomationData
import com.robotutor.nexora.module.automation.domain.vo.component.data.ComponentData
import com.robotutor.nexora.module.automation.domain.vo.component.data.FeedControlData
import com.robotutor.nexora.module.automation.domain.vo.component.data.FeedValueData
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import com.robotutor.nexora.shared.domain.specification.AndSpecification
import com.robotutor.nexora.shared.domain.specification.NotSpecification
import com.robotutor.nexora.shared.domain.specification.OrSpecification
import com.robotutor.nexora.shared.domain.specification.Specification
import org.springframework.stereotype.Service

@Service
class CreateAutomationPolicy : Policy<ResolvedAutomation> {
    override fun evaluate(input: ResolvedAutomation): PolicyResult {
        val reasons = mutableListOf<String>()
        input.triggers.forEach { reasons.addAll(evaluateComponent(it).getReasons()) }
        input.actions.forEach { reasons.addAll(evaluateComponent(it).getReasons()) }
        reasons.addAll(evaluateSpecification(input.condition).getReasons())

        return PolicyResult.create(reasons)
    }

    private fun evaluateComponent(componentData: ComponentData<out Component>): PolicyResult {
        val reasons = mutableListOf<String>()
        when (componentData) {
            is AutomationData -> Unit
            is FeedControlData -> Unit
            is FeedValueData -> Unit
        }

        return PolicyResult.create(reasons)
    }

    private fun evaluateSpecification(specification: Specification<ComponentData<Condition>>?): PolicyResult {
        if (specification == null) return PolicyResult.allow()

        val reasons = mutableListOf<String>()

        when (specification) {
            is AndSpecification -> specification.specifications
                .forEach { reasons.addAll(evaluateSpecification(it).getReasons()) }

            is OrSpecification -> specification.specifications
                .forEach { reasons.addAll(evaluateSpecification(it).getReasons()) }

            is NotSpecification -> reasons.addAll(evaluateSpecification(specification.specification).getReasons())

            else -> Unit
        }

        return PolicyResult.create(reasons)
    }
}