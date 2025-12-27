package com.robotutor.nexora.context.iam.application.policy

import com.robotutor.nexora.context.iam.application.command.RegisterPremisesOwnerCommand
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterPremisesResourcePolicy : Policy<RegisterPremisesOwnerCommand> {
    override fun evaluate(command: RegisterPremisesOwnerCommand): Mono<PolicyResult> {
        return Mono.just(PolicyResult.allow())
    }
}