package com.robotutor.nexora.module.iam.domain.policy

import com.robotutor.nexora.module.iam.domain.policy.context.DuplicateAccountContext
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import org.springframework.stereotype.Service

@Service
class RegisterAccountPolicy : Policy<DuplicateAccountContext> {
    override fun evaluate(input: DuplicateAccountContext): PolicyResult {
        return if (input.accountAlreadyExists) {
            PolicyResult.deny(listOf("Account with credentialId ${input.credentialId.value} already exists"))
        }
        else {
            PolicyResult.allow()
        }
    }
}