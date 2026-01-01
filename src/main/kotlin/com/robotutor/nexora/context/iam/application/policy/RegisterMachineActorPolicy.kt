package com.robotutor.nexora.context.iam.application.policy

import com.robotutor.nexora.context.iam.application.command.RegisterMachineActorCommand
import com.robotutor.nexora.context.iam.domain.repository.ActorRepository
import com.robotutor.nexora.context.iam.domain.specification.ActorByAccountIdSpecification
import com.robotutor.nexora.context.iam.domain.specification.ActorByPremisesIdSpecification
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import com.robotutor.nexora.shared.domain.vo.principal.AccountType
import com.robotutor.nexora.shared.utility.createMono
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterMachineActorPolicy(private val actorRepository: ActorRepository) : Policy<RegisterMachineActorCommand> {
    override fun evaluate(input: RegisterMachineActorCommand): Mono<PolicyResult> {
        if (input.owner.type != AccountType.MACHINE)
            return createMono(PolicyResult.deny(listOf("Account is not MACHINE type")))

        val specification = ActorByAccountIdSpecification(input.owner.accountId)
            .and(ActorByPremisesIdSpecification(input.premisesId))
        return actorRepository.findBySpecification(specification)
            .map { PolicyResult.deny(listOf("Actor already exists")) }
            .switchIfEmpty(createMono(PolicyResult.allow()))
    }
}