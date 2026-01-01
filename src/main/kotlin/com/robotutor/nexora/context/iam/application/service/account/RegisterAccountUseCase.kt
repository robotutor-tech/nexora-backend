package com.robotutor.nexora.context.iam.application.service.account

import com.robotutor.nexora.context.iam.application.command.RegisterAccountCommand
import com.robotutor.nexora.context.iam.application.policy.RegisterAccountPolicy
import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.context.iam.domain.event.AccountRegistrationFailedEvent
import com.robotutor.nexora.context.iam.domain.event.IAMEventPublisher
import com.robotutor.nexora.context.iam.domain.exception.IAMError
import com.robotutor.nexora.context.iam.domain.repository.AccountIdGenerator
import com.robotutor.nexora.context.iam.domain.repository.AccountRepository
import com.robotutor.nexora.context.iam.domain.service.SecretEncoder
import com.robotutor.nexora.context.iam.domain.vo.Credential
import com.robotutor.nexora.shared.domain.event.publishEventOnError
import com.robotutor.nexora.shared.domain.utility.errorOnDenied
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterAccountUseCase(
    private val registerAccountPolicy: RegisterAccountPolicy,
    private val accountIdGenerator: AccountIdGenerator,
    private val accountRepository: AccountRepository,
    private val secretService: SecretEncoder,
    private val eventPublisher: IAMEventPublisher,
    
) {
    private val logger = Logger(this::class.java)

    fun execute(command: RegisterAccountCommand): Mono<AccountAggregate> {
        return registerAccountPolicy.evaluate(command)
            .errorOnDenied(IAMError.NEXORA0201)
            .flatMap { accountIdGenerator.generate() }
            .map { accountId ->
                AccountAggregate.register(
                    accountId = accountId,
                    type = command.type,
                    principalId = command.principalId,
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
            .publishEventOnError(eventPublisher, AccountRegistrationFailedEvent(command.type, command.principalId))
            .logOnSuccess(logger, "Successfully registered account")
            .logOnError(logger, "Failed to register account")
    }
}