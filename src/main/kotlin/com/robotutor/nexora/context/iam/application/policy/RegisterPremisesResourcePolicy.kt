package com.robotutor.nexora.context.iam.application.policy

import com.robotutor.nexora.context.iam.application.command.RegisterOwnerCommand
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterPremisesResourcePolicy : Policy<RegisterOwnerCommand> {
    override fun evaluate(command: RegisterOwnerCommand): Mono<PolicyResult> {
        return Mono.just(PolicyResult.allow())
    }
}