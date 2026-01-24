package com.robotutor.nexora.module.automation.domain.policy

import com.robotutor.nexora.module.automation.domain.entity.ResolvedAutomation
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import org.springframework.stereotype.Service

@Service
class ExecuteAutomationPolicy(
    private val createAutomationPolicy: CreateAutomationPolicy,
) : Policy<ResolvedAutomation> {
    override fun evaluate(input: ResolvedAutomation): PolicyResult {
        return createAutomationPolicy.evaluate(input)
    }
}

