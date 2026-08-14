package com.robotutor.nexora.module.identity.application.service.account

import com.robotutor.nexora.module.device.domain.vo.DeviceId
import com.robotutor.nexora.module.identity.application.command.AuthenticateAccountCommand
import com.robotutor.nexora.module.identity.application.command.CreateSessionCommand
import com.robotutor.nexora.module.identity.application.service.CreateSessionService
import com.robotutor.nexora.module.identity.domain.aggregate.AccountAggregate
import com.robotutor.nexora.module.identity.domain.exception.IAMError
import com.robotutor.nexora.module.identity.domain.repository.AccountRepository
import com.robotutor.nexora.module.identity.domain.service.SecretEncoder
import com.robotutor.nexora.module.identity.domain.vo.SessionId
import com.robotutor.nexora.module.user.domain.vo.UserId
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.domain.vo.Tokens
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.shared.domain.vo.principal.DeviceData
import com.robotutor.nexora.shared.domain.vo.principal.SubjectType
import com.robotutor.nexora.shared.domain.vo.principal.UserData
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
import com.robotutor.nexora.shared.utility.required
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthenticateAccountService(
    private val accountRepository: AccountRepository,
    private val secretService: SecretEncoder,
    private val createSessionService: CreateSessionService,
) {
    private val logger = Logger(this::class.java)

    fun execute(command: AuthenticateAccountCommand): Mono<Tokens> {
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
                val sessionCommand = CreateSessionCommand(createAccountData(account), SessionId.generate())
                createSessionService.execute(sessionCommand)
//                val sessionId = SessionId.generate()
//
//                val refreshToken = TokenValue.generate(240)
//                val session = sessionService.create(account, refreshToken)
//                val event = AccountAuthenticatedEvent(account.accountId, account.type, account.subjectId)
//                sessionRepository.save(session)
//                    .publishEvent(eventPublisher, event)
//                    .map { SessionTokens(session.getAccessToken(), refreshToken) }
            }
            .logOnSuccess(logger, "Successfully authenticated account")
            .logOnError(logger, "Failed to authenticate account")
    }

    private fun createAccountData(account: AccountAggregate): AccountData {
        return when (account.type) {
            SubjectType.USER -> UserData(UserId.from(account.subjectId), account.accountId)
            SubjectType.DEVICE -> DeviceData(DeviceId.from(account.subjectId), account.accountId)
        }
    }
}
