package com.robotutor.nexora.module.automation.domain.policy

import com.robotutor.nexora.module.automation.domain.entity.ResolvedAutomation
import com.robotutor.nexora.module.automation.domain.vo.component.Component
import com.robotutor.nexora.module.automation.domain.vo.component.Condition
import com.robotutor.nexora.module.automation.domain.vo.component.data.AutomationData
import com.robotutor.nexora.module.automation.domain.vo.component.data.ComponentData
import com.robotutor.nexora.module.automation.domain.vo.component.data.FeedControlData
import com.robotutor.nexora.module.automation.domain.vo.component.data.FeedValueData
import com.robotutor.nexora.module.automation.domain.vo.component.specification.Specification
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import org.springframework.stereotype.Service

@Service
class CreateAutomationPolicy : Policy<ResolvedAutomation> {
    override fun evaluate(input: ResolvedAutomation): PolicyResult {
        val reasons = mutableListOf<String>()
        input.triggers.forEach { reasons.addAll(evaluate(it).getReasons()) }
        input.actions.forEach { reasons.addAll(evaluate(it).getReasons()) }
        reasons.addAll(evaluate(input.condition).getReasons())
        return PolicyResult.create(reasons)
    }

    private fun evaluate(componentData: ComponentData<out Component>): PolicyResult {
        when (componentData) {
            is AutomationData -> TODO()
            is FeedControlData -> TODO()
            is FeedValueData -> TODO()
        }
        return PolicyResult.allow()
    }

    private fun evaluate(specification: Specification<ComponentData<Condition>>?): PolicyResult {
        if (specification == null) return PolicyResult.allow()
        return PolicyResult.allow()
    }
}