package com.robotutor.nexora.context.iam.application.usecase

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.context.iam.application.command.AuthenticateAccountCommand
import com.robotutor.nexora.context.iam.application.command.CreateSessionCommand
import com.robotutor.nexora.context.iam.application.view.SessionTokens
import com.robotutor.nexora.context.iam.domain.event.AccountAuthenticatedEvent
import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.context.iam.domain.repository.AccountRepository
import com.robotutor.nexora.context.iam.domain.service.SecretEncoder
import com.robotutor.nexora.context.iam.domain.vo.AccountPrincipal
import com.robotutor.nexora.modules.iam.exceptions.NexoraError
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthenticateAccountUseCase(
    private val accountRepository: AccountRepository,
    private val secretService: SecretEncoder,
    private val createSessionUseCase: CreateSessionUseCase,
    private val eventPublisher: EventPublisher<IAMEvent>
) {
    private val logger = Logger(this::class.java)

    fun execute(command: AuthenticateAccountCommand): Mono<SessionTokens> {
        return accountRepository.findByCredentialIdAndKind(command.credentialId, command.kind)
            .flatMap { account ->
                val credential =
                    account.credentials.find { it.kind == command.kind && it.credentialId == command.credentialId }
                val matchResult = credential?.let { !secretService.matches(command.secret, credential.secret) } ?: false
                if (matchResult) {
                    createMonoError(UnAuthorizedException(NexoraError.NEXORA0202))
                } else {
                    createMono(account)
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