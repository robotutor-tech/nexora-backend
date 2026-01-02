package com.robotutor.nexora.module.iam.domain.policy

import com.robotutor.nexora.module.iam.application.command.RegisterPremisesOwnerCommand
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import org.springframework.stereotype.Service

@Service
class RegisterPremisesResourcePolicy : Policy<RegisterPremisesOwnerCommand> {
    override fun evaluate(input: RegisterPremisesOwnerCommand): PolicyResult {
        return PolicyResult.allow()
    }
}