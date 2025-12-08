package com.robotutor.nexora.context.iam.application.policy

import com.robotutor.nexora.context.iam.application.command.RegisterPremisesResourceCommand
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterPremisesResourcePolicy : Policy<RegisterPremisesResourceCommand> {
    override fun evaluate(command: RegisterPremisesResourceCommand): Mono<PolicyResult> {
        return Mono.just(PolicyResult.allow())
    }
}