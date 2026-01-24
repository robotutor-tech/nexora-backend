package com.robotutor.nexora.module.iam.application.service.account

import com.robotutor.nexora.module.iam.application.command.AuthenticateAccountCommand
import com.robotutor.nexora.module.iam.application.view.SessionTokens
import com.robotutor.nexora.module.iam.domain.event.AccountAuthenticatedEvent
import com.robotutor.nexora.module.iam.domain.event.IAMEventPublisher
import com.robotutor.nexora.module.iam.domain.exception.IAMError
import com.robotutor.nexora.module.iam.domain.repository.AccountRepository
import com.robotutor.nexora.module.iam.domain.repository.SessionRepository
import com.robotutor.nexora.module.iam.domain.service.SecretEncoder
import com.robotutor.nexora.module.iam.domain.service.SessionService
import com.robotutor.nexora.module.iam.domain.vo.TokenValue
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
import com.robotutor.nexora.shared.utility.required
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthenticateAccountService(
    private val accountRepository: AccountRepository,
    private val secretService: SecretEncoder,
    private val sessionService: SessionService,
    private val sessionRepository: SessionRepository,
    private val eventPublisher: IAMEventPublisher,
) {
    private val logger = Logger(this::class.java)

    fun execute(command: AuthenticateAccountCommand): Mono<SessionTokens> {
        return accountRepository.findByCredentialId(command.credentialId)
            .required(UnAuthorizedException(IAMError.NEXORA0202))
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
                val refreshToken = TokenValue.generate(240)
                val session = sessionService.create(account, refreshToken)
                val event = AccountAuthenticatedEvent(account.accountId, account.type, account.principalId)
                sessionRepository.save(session)
                    .publishEvent(eventPublisher, event)
                    .map { SessionTokens(session.getAccessToken(), refreshToken) }
            }
            .logOnSuccess(logger, "Successfully authenticated account")
            .logOnError(logger, "Failed to authenticate account")
    }
}