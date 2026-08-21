package com.robotutor.nexora.module.identity.application.service.account

import com.robotutor.nexora.module.identity.application.command.RegisterUserAccountCommand
import com.robotutor.nexora.module.identity.application.facade.UserFacade
import com.robotutor.nexora.module.identity.domain.aggregate.Account
import com.robotutor.nexora.module.identity.domain.event.AccountCreationFailedEvent
import com.robotutor.nexora.module.identity.domain.event.IdentityEvent
import com.robotutor.nexora.module.identity.domain.exception.IdentityError
import com.robotutor.nexora.module.identity.domain.policy.RegisterUserAccountPolicy
import com.robotutor.nexora.module.identity.domain.policy.context.RegisterUserAccountPolicyContext
import com.robotutor.nexora.module.identity.domain.repository.AccountIdGenerator
import com.robotutor.nexora.module.identity.domain.repository.AccountRepository
import com.robotutor.nexora.module.identity.domain.service.SecretEncoder
import com.robotutor.nexora.module.identity.domain.vo.Credential
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.utility.enforcePolicy
import com.robotutor.nexora.shared.domain.vo.AccountType
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.domain.vo.UserData
import com.robotutor.nexora.shared.message.mapper.EventMapper
import com.robotutor.nexora.shared.outbox.auditOnSuccess
import com.robotutor.nexora.shared.outbox.publishEventOnError
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class RegisterUserAccountService(
    private val registerUserAccountPolicy: RegisterUserAccountPolicy,
    private val accountIdGenerator: AccountIdGenerator,
    private val accountRepository: AccountRepository,
    private val secretEncoder: SecretEncoder,
    private val userFacade: UserFacade,
    private val eventMapper: EventMapper<IdentityEvent>
) {
    private val logger = Logger(this::class.java)

    @Transactional
    fun execute(command: RegisterUserAccountCommand): Mono<Account> {
        return accountRepository.existsByCredentialId(command.email)
            .enforcePolicy(registerUserAccountPolicy, IdentityError.NEXORA0201) {
                RegisterUserAccountPolicyContext(it, command.email)
            }
            .flatMap { userFacade.register(command) }
            .flatMap { user ->
                accountIdGenerator.generate()
                    .map { accountId ->
                        val hashedPassword = secretEncoder.encode(command.password)
                        val credential = Credential(command.email, hashedPassword)
                        Account.register(accountId, AccountType.USER, user.userId, credential)
                    }
                    .flatMap { account ->
                        accountRepository.save(account)
                            .auditOnSuccess(
                                "USER_ACCOUNT_CREATED",
                                ResourceType.ACCOUNT,
                                account.accountId,
                                principal = UserData(user.userId, account.accountId)
                            )
                            .logOnSuccess(
                                logger,
                                "Successfully registered account",
                                mapOf("userId" to user.userId.value, "accountId" to account.accountId.value)
                            )
                    }
                    .publishEventOnError(AccountCreationFailedEvent(AccountType.USER, user.userId), eventMapper)
            }
            .logOnError(logger, "Failed to register account")
    }
}
