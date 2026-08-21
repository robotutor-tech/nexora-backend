package com.robotutor.nexora.module.identity.domain.policy

import com.robotutor.nexora.module.identity.domain.policy.context.RegisterUserAccountPolicyContext
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import org.springframework.stereotype.Service

@Service
class RegisterUserAccountPolicy : Policy<RegisterUserAccountPolicyContext> {
    override fun evaluate(input: RegisterUserAccountPolicyContext): PolicyResult {
        return if (input.exists) {
            PolicyResult.deny(listOf("User is already registered with ${input.credentialId.value}"))
        } else {
            PolicyResult.allow()
        }
    }
}
