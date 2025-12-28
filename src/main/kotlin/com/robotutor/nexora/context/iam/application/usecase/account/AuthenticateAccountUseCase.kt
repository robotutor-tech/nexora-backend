package com.robotutor.nexora.context.iam.application.usecase.account

import com.robotutor.nexora.context.iam.application.command.AuthenticateAccountCommand
import com.robotutor.nexora.context.iam.application.command.CreateSessionCommand
import com.robotutor.nexora.context.iam.application.usecase.CreateSessionUseCase
import com.robotutor.nexora.context.iam.application.view.SessionTokens
import com.robotutor.nexora.context.iam.domain.event.AccountAuthenticatedEvent
import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.context.iam.domain.exception.IAMError
import com.robotutor.nexora.context.iam.domain.repository.AccountRepository
import com.robotutor.nexora.context.iam.domain.service.SecretEncoder
import com.robotutor.nexora.context.iam.domain.vo.AccountPrincipal
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.application.observability.AppLoggerFactory
import com.robotutor.nexora.shared.application.observability.logOnError
import com.robotutor.nexora.shared.application.observability.logOnSuccess
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthenticateAccountUseCase(
    private val accountRepository: AccountRepository,
    private val secretService: SecretEncoder,
    private val createSessionUseCase: CreateSessionUseCase,
    private val eventPublisher: EventPublisher<IAMEvent>,
    loggerFactory: AppLoggerFactory,
) {
    private val logger = loggerFactory.forClass(this::class.java)

    fun execute(command: AuthenticateAccountCommand): Mono<SessionTokens> {
        return accountRepository.findByCredentialId(command.credentialId)
            .flatMap { account ->
                val credential = account.getCredential(command.credentialId)
                val matchResult = secretService.matches(command.secret, credential.secret)
                if (matchResult) {
                    createMono(account)
                } else {
                    createMonoError(UnAuthorizedException(IAMError.NEXORA0202))
                }
            }
            .flatMap { account ->
                val createSessionCommand =
                    CreateSessionCommand(AccountPrincipal(account.accountId, account.type, account.principalId))
                val event = AccountAuthenticatedEvent(account.accountId, account.type, account.principalId)
                createSessionUseCase.execute(createSessionCommand)
                    .publishEvent(eventPublisher, event)
            }
            .logOnSuccess(logger, "Successfully authenticated account")
            .logOnError(logger, "Failed to authenticate account")
    }
}