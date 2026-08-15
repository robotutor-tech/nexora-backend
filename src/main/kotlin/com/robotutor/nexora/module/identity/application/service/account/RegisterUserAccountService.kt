package com.robotutor.nexora.module.identity.application.service.account

import com.robotutor.nexora.module.identity.application.command.RegisterUserAccountCommand
import com.robotutor.nexora.module.identity.domain.aggregate.Account
import com.robotutor.nexora.module.identity.domain.exception.IdentityError
import com.robotutor.nexora.module.identity.domain.policy.RegisterAccountPolicy
import com.robotutor.nexora.module.identity.domain.policy.context.RegisterAccountPolicyContext
import com.robotutor.nexora.module.identity.domain.repository.AccountIdGenerator
import com.robotutor.nexora.module.identity.domain.repository.AccountRepository
import com.robotutor.nexora.module.identity.domain.service.SecretEncoder
import com.robotutor.nexora.module.identity.domain.specification.AccountByCredentialIdSpecification
import com.robotutor.nexora.module.identity.domain.specification.AccountBySubjectIdSpecification
import com.robotutor.nexora.module.identity.domain.vo.Credential
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.utility.enforcePolicy
import com.robotutor.nexora.shared.domain.vo.SubjectType
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterUserAccountService(
    private val registerAccountPolicy: RegisterAccountPolicy,
    private val accountIdGenerator: AccountIdGenerator,
    private val accountRepository: AccountRepository,
    private val secretEncoder: SecretEncoder,
//    private val eventPublisher: IdentityEventPublisher,
) {
    private val logger = Logger(this::class.java)

    fun execute(command: RegisterUserAccountCommand): Mono<Account> {
        val specification = AccountByCredentialIdSpecification(command.email)
            .or(AccountBySubjectIdSpecification(command.userId))
        return accountRepository.findAll(specification)
            .collectList()
            .enforcePolicy(registerAccountPolicy, IdentityError.NEXORA0201) {
                RegisterAccountPolicyContext(it, command.email, command.userId)
            }
            .flatMap { accountIdGenerator.generate() }
            .map { accountId ->
                val hashedPassword = secretEncoder.encode(command.password)
                Account.register(
                    accountId = accountId,
                    type = SubjectType.USER,
                    subjectId = command.userId,
                    credential = Credential(command.email, hashedPassword),
                )
            }
            .flatMap { accountAggregate -> accountRepository.save(accountAggregate) }
//            .publishEventOnError(eventPublisher, AccountRegistrationFailedEvent(command.type, command.subjectId))
            .logOnSuccess(logger, "Successfully registered account")
            .logOnError(logger, "Failed to register account")
    }
}
