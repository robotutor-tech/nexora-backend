package com.robotutor.nexora.context.iam.application.usecase

import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.context.iam.application.command.GetAccountQuery
import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.context.iam.domain.repository.AccountRepository
import com.robotutor.nexora.modules.iam.exceptions.NexoraError
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class GetAccountUseCase(
    private val accountRepository: AccountRepository,
) {
    private val logger = Logger(this::class.java)

    fun execute(query: GetAccountQuery): Mono<AccountAggregate> {
        return accountRepository.findByAccountId(query.accountId)
            .switchIfEmpty(createMonoError(DataNotFoundException(NexoraError.NEXORA0203)))
            .logOnSuccess(logger, "Successfully retrieved account with accountId: ${query.accountId}")
            .logOnError(logger, "", "Failed to retrieve account with accountId: ${query.accountId}")
    }
}