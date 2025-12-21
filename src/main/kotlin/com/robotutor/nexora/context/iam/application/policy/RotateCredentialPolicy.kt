package com.robotutor.nexora.context.iam.application.policy

import com.robotutor.nexora.context.iam.application.command.RotateCredentialCommand
import com.robotutor.nexora.context.iam.domain.repository.AccountRepository
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import com.robotutor.nexora.shared.domain.vo.AccountType
import com.robotutor.nexora.shared.utility.createMono
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RotateCredentialPolicy(private val accountRepository: AccountRepository) : Policy<RotateCredentialCommand> {
    override fun evaluate(command: RotateCredentialCommand): Mono<PolicyResult> {
        return accountRepository.findByAccountId(command.accountId)
            .map {
                val reasons = mutableListOf<String>()
                if (it.type != AccountType.MACHINE) {
                    reasons.add("Account is not MACHINE type")
                }
                if (it.createdBy != command.actorData.actorId) {
                    reasons.add("Account is not created by the actor")
                }
                if (reasons.isEmpty()) PolicyResult.allow() else PolicyResult.deny(reasons)
            }
            .switchIfEmpty(createMono(PolicyResult.deny(listOf("Account not found"))))
    }
}