package com.robotutor.nexora.context.iam.application.usecase.account

import com.robotutor.nexora.context.iam.application.command.ActivateAccountCommand
import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.context.iam.domain.repository.AccountRepository
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActivateAccountUseCase(
    private val accountRepository: AccountRepository,
) {
    private val logger = Logger(this::class.java)

    fun execute(command: ActivateAccountCommand): Mono<AccountAggregate> {
        return accountRepository.findByAccountId(command.accountId)
            .map { account -> account.activate() }
            .flatMap { accountAggregate -> accountRepository.save(accountAggregate) }
            .logOnSuccess(logger, "Successfully registered account")
            .logOnError(logger, "Failed to register account")
    }
}