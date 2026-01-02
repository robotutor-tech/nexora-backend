package com.robotutor.nexora.module.iam.domain.policy

import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import org.springframework.stereotype.Service

@Service
class RegisterPremisesOwnerPolicy : Policy<Boolean> {
    override fun evaluate(input: Boolean): PolicyResult {
        return if (input) {
            PolicyResult.deny(listOf("Premises resources already exists"))
        } else (PolicyResult.allow())
    }
}