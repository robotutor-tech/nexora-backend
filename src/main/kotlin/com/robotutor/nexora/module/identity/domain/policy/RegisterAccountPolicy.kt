package com.robotutor.nexora.module.identity.domain.policy

import com.robotutor.nexora.module.identity.domain.policy.context.RegisterAccountPolicyContext
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import org.springframework.stereotype.Service

@Service
class RegisterAccountPolicy : Policy<RegisterAccountPolicyContext> {
    override fun evaluate(input: RegisterAccountPolicyContext): PolicyResult {
        return if (input.accounts.isNotEmpty()) {
            PolicyResult.deny(listOf("Account with credentialId ${input.credentialId.value} already exists"))
        } else {
            PolicyResult.allow()
        }
    }
}
