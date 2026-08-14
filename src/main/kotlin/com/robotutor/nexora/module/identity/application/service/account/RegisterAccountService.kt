package com.robotutor.nexora.module.identity.application.service.account

import com.robotutor.nexora.module.identity.application.command.RegisterAccountCommand
import com.robotutor.nexora.module.identity.domain.policy.RegisterAccountPolicy
import com.robotutor.nexora.module.identity.domain.aggregate.AccountAggregate
import com.robotutor.nexora.module.identity.domain.event.AccountRegistrationFailedEvent
import com.robotutor.nexora.module.identity.domain.event.IAMEventPublisher
import com.robotutor.nexora.module.identity.domain.exception.IAMError
import com.robotutor.nexora.module.identity.domain.policy.context.DuplicateAccountContext
import com.robotutor.nexora.module.identity.domain.repository.AccountIdGenerator
import com.robotutor.nexora.module.identity.domain.repository.AccountRepository
import com.robotutor.nexora.module.identity.domain.service.SecretEncoder
import com.robotutor.nexora.module.identity.domain.vo.Credential
import com.robotutor.nexora.shared.domain.event.publishEventOnError
import com.robotutor.nexora.shared.domain.utility.enforcePolicy
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterAccountService(
    private val registerAccountPolicy: RegisterAccountPolicy,
    private val accountIdGenerator: AccountIdGenerator,
    private val accountRepository: AccountRepository,
    private val secretService: SecretEncoder,
    private val eventPublisher: IAMEventPublisher,
) {
    private val logger = Logger(this::class.java)

    fun execute(command: RegisterAccountCommand): Mono<AccountAggregate> {
        return accountRepository.existsByCredentialId(command.credentialId)
            .enforcePolicy(
                registerAccountPolicy,
                { DuplicateAccountContext(it, command.credentialId) },
                IAMError.NEXORA0201
            )
            .flatMap { accountIdGenerator.generate() }
            .map { accountId ->
                AccountAggregate.register(
                    accountId = accountId,
                    type = command.type,
                    subjectId = command.subjectId,
                    credentials = listOf(
                        Credential(
                            kind = command.kind,
                            credentialId = command.credentialId,
                            secret = secretService.encode(command.secret)
                        )
                    ),
                    createdBy = command.createdBy
                )
            }
            .flatMap { accountAggregate -> accountRepository.save(accountAggregate) }
            .publishEventOnError(eventPublisher, AccountRegistrationFailedEvent(command.type, command.subjectId))
            .logOnSuccess(logger, "Successfully registered account")
            .logOnError(logger, "Failed to register account")
    }
}
