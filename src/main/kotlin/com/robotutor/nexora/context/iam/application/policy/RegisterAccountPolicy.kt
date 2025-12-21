package com.robotutor.nexora.context.iam.application.policy

import com.robotutor.nexora.context.iam.application.command.RegisterAccountCommand
import com.robotutor.nexora.context.iam.domain.repository.AccountRepository
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import com.robotutor.nexora.shared.utility.createMono
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterAccountPolicy(private val accountRepository: AccountRepository) : Policy<RegisterAccountCommand> {
    override fun evaluate(command: RegisterAccountCommand): Mono<PolicyResult> {
        return accountRepository.findByCredentialId(command.credentialId)
            .map { PolicyResult.deny(listOf("Account with credentialId ${command.credentialId.value} already exists")) }
            .switchIfEmpty(createMono(PolicyResult.allow()))
    }
}