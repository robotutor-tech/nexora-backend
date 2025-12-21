package com.robotutor.nexora.context.iam.application.usecase.account

import com.robotutor.nexora.context.iam.application.command.AuthenticateAccountCommand
import com.robotutor.nexora.context.iam.application.command.CreateSessionCommand
import com.robotutor.nexora.context.iam.application.usecase.CreateSessionUseCase
import com.robotutor.nexora.context.iam.application.view.SessionTokens
import com.robotutor.nexora.context.iam.domain.event.AccountAuthenticatedEvent
import com.robotutor.nexora.context.iam.domain.event.IAMBusinessEvent
import com.robotutor.nexora.context.iam.domain.exception.IAMError
import com.robotutor.nexora.context.iam.domain.repository.AccountRepository
import com.robotutor.nexora.context.iam.domain.service.SecretEncoder
import com.robotutor.nexora.context.iam.domain.vo.AccountPrincipal
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthenticateAccountUseCase(
    private val accountRepository: AccountRepository,
    private val secretService: SecretEncoder,
    private val createSessionUseCase: CreateSessionUseCase,
    private val eventPublisher: EventPublisher<IAMBusinessEvent>
) {
    private val logger = Logger(this::class.java)

    fun execute(command: AuthenticateAccountCommand): Mono<SessionTokens> {
        return accountRepository.findByCredentialIdAndKind(command.credentialId, command.kind)
            .flatMap { account ->
                val credential = account.getCredential(command.kind, command.credentialId)
                val matchResult = secretService.matches(command.secret, credential.secret)
                if (matchResult) {
                    createMono(account)
                } else {
                    createMonoError(UnAuthorizedException(IAMError.NEXORA0202))
                }
            }
            .flatMap { account ->
                val createSessionCommand = CreateSessionCommand(AccountPrincipal(account.accountId, account.type))
                createSessionUseCase.execute(createSessionCommand)
                    .publishEvent(eventPublisher, AccountAuthenticatedEvent(account.accountId, account.type))
            }
            .logOnSuccess(logger, "Successfully authenticated account")
            .logOnError(logger, "Failed to authenticate account")
    }
}