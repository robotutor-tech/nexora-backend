package com.robotutor.nexora.module.identity.domain.policy

import com.robotutor.nexora.module.identity.domain.policy.context.MatchPasswordPolicyContext
import com.robotutor.nexora.module.identity.domain.service.SecretEncoder
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import org.springframework.stereotype.Service

@Service
class MatchPasswordPolicy(private val secretEncoder: SecretEncoder) : Policy<MatchPasswordPolicyContext> {
    override fun evaluate(input: MatchPasswordPolicyContext): PolicyResult {
        val matchResult = secretEncoder.matches(input.rawPassword, input.hashedPassword)
        if (matchResult) {
            return PolicyResult.allow()
        }
        return PolicyResult.deny(emptyList())
    }
}
