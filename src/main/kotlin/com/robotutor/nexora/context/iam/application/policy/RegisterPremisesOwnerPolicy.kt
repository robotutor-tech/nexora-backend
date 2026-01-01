package com.robotutor.nexora.context.iam.application.policy

import com.robotutor.nexora.context.iam.application.command.RegisterPremisesOwnerCommand
import com.robotutor.nexora.context.iam.domain.repository.ActorRepository
import com.robotutor.nexora.context.iam.domain.specification.ActorByPremisesIdSpecification
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import com.robotutor.nexora.shared.utility.createMono
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterPremisesOwnerPolicy(private val actorRepository: ActorRepository) : Policy<RegisterPremisesOwnerCommand> {
    override fun evaluate(input: RegisterPremisesOwnerCommand): Mono<PolicyResult> {
        return actorRepository.findBySpecification(ActorByPremisesIdSpecification(input.premisesId))
            .map { PolicyResult.deny(listOf("Premises resources already exists")) }
            .switchIfEmpty(createMono(PolicyResult.allow()))
    }
}