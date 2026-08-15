package com.robotutor.nexora.module.identity.application.service.account

import com.robotutor.nexora.module.identity.application.command.GetAccountQuery
import com.robotutor.nexora.module.identity.domain.aggregate.Account
import com.robotutor.nexora.module.identity.domain.exception.IdentityError
import com.robotutor.nexora.module.identity.domain.repository.AccountRepository
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.utility.required
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class GetAccountService(
    private val accountRepository: AccountRepository,
) {
    private val logger = Logger(this::class.java)

    fun execute(query: GetAccountQuery): Mono<Account> {
        return accountRepository.findByAccountId(query.accountId)
            .required(DataNotFoundException(IdentityError.NEXORA0203))
            .logOnSuccess(logger, "Successfully retrieved account", mapOf("accountId" to query.accountId))
            .logOnError(logger, "Failed to retrieve account", mapOf("accountId" to query.accountId))
    }
}
