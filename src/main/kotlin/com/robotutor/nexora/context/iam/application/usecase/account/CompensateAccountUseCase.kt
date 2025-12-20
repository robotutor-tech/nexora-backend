package com.robotutor.nexora.context.iam.application.usecase.account

import com.robotutor.nexora.context.iam.application.command.CompensateAccountCommand
import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.context.iam.domain.event.AccountCompensatedEvent
import com.robotutor.nexora.context.iam.domain.event.IAMBusinessEvent
import com.robotutor.nexora.context.iam.domain.repository.AccountRepository
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.infrastructure.messaging.BusinessEventPublisher
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CompensateAccountUseCase(
    private val accountRepository: AccountRepository,
    private val eventPublisher: BusinessEventPublisher<IAMBusinessEvent>
) {
    private val logger = Logger(this::class.java)

    fun execute(command: CompensateAccountCommand): Mono<AccountAggregate> {
        return accountRepository.deleteByAccountId(command.accountId)
            .publishEvent(eventPublisher, AccountCompensatedEvent(command.accountId))
            .logOnSuccess(logger, "Successfully compensated account", mapOf("accountId" to command.accountId))
            .logOnError(logger, "Failed to compensate account", mapOf("accountId" to command.accountId))
    }
}