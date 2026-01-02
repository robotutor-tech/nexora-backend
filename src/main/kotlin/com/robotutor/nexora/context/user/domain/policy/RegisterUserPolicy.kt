package com.robotutor.nexora.context.user.domain.policy

import com.robotutor.nexora.context.user.domain.policy.context.DuplicateUserContext
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import org.springframework.stereotype.Component

@Component
class RegisterUserPolicy : Policy<DuplicateUserContext> {
    override fun evaluate(input: DuplicateUserContext): PolicyResult {
        val reasons = mutableListOf<String>()
        if (input.userAlreadyExists) {
            reasons.add("User with email ${input.email.value} already exists")
        }
        return PolicyResult.create(reasons)
    }
}