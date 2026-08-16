package com.robotutor.nexora.module.identity.application.service.account

import com.robotutor.nexora.module.identity.application.command.AuthenticateAccountCommand
import com.robotutor.nexora.module.identity.application.command.CreateSessionCommand
import com.robotutor.nexora.module.identity.application.service.CreateSessionService
import com.robotutor.nexora.module.identity.domain.aggregate.Account
import com.robotutor.nexora.module.identity.domain.exception.IdentityError
import com.robotutor.nexora.module.identity.domain.policy.MatchPasswordPolicy
import com.robotutor.nexora.module.identity.domain.policy.context.MatchPasswordPolicyContext
import com.robotutor.nexora.module.identity.domain.repository.AccountRepository
import com.robotutor.nexora.module.identity.domain.service.SecretEncoder
import com.robotutor.nexora.module.identity.domain.vo.SessionId
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.domain.utility.enforcePolicy
import com.robotutor.nexora.shared.domain.vo.*
import com.robotutor.nexora.shared.outbox.auditOnSuccess
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
import com.robotutor.nexora.shared.utility.required
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class AuthenticateAccountService(
    private val accountRepository: AccountRepository,
    private val createSessionService: CreateSessionService,
    private val matchPasswordPolicy: MatchPasswordPolicy
) {
    private val logger = Logger(this::class.java)

    @Transactional
    fun execute(command: AuthenticateAccountCommand): Mono<Tokens> {
        return accountRepository.findByCredentialId(command.credentialId)
            .required(UnAuthorizedException(IdentityError.NEXORA0202))
            .enforcePolicy(matchPasswordPolicy, UnAuthorizedException(IdentityError.NEXORA0202)) {
                MatchPasswordPolicyContext(command.secret, it.credential.hashedSecret)
            }
            .flatMap { account ->
                val accountData = createAccountData(account)
                val sessionCommand = CreateSessionCommand(accountData, SessionId.generate())
                createSessionService.execute(sessionCommand)
                    .auditOnSuccess(
                        "ACCOUNT_AUTHENTICATED",
                        ResourceType.ACCOUNT,
                        account.accountId,
                        principal = accountData
                    )
            }
            .logOnSuccess(logger, "Successfully authenticated account")
            .logOnError(logger, "Failed to authenticate account")
    }

    private fun createAccountData(account: Account): AccountData {
        return when (account.accountType) {
            AccountType.USER -> UserData.from(account.accountId, account.subjectId)
            AccountType.DEVICE -> DeviceData.from(account.accountId, account.subjectId)
        }
    }
}
