package com.robotutor.nexora.context.premises.application.policy

import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.context.premises.application.command.RegisterPremisesCommand
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterPremisesPolicy : Policy<RegisterPremisesCommand> {
    override fun evaluate(input: RegisterPremisesCommand): Mono<PolicyResult> {
        val reasons = mutableListOf<String>()
        if (!input.owner.isHuman()) {
            reasons.add("Only humans can register premises")
        }
        val policyResult = if (reasons.isEmpty()) PolicyResult.allow() else PolicyResult.deny(reasons)
        return createMono(policyResult)
    }

}