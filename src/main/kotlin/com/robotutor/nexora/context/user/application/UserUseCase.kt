package com.robotutor.nexora.context.user.application

import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.context.user.application.command.GetUserQuery
import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.domain.exception.NexoraError
import com.robotutor.nexora.context.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserUseCase(
    private val userRepository: UserRepository,
) {
    val logger = Logger(this::class.java)

    fun execute(query: GetUserQuery): Mono<UserAggregate> {
        return userRepository.findByAccountId(query.accountId)
            .switchIfEmpty(createMonoError(DataNotFoundException(NexoraError.NEXORA0205)))
            .logOnSuccess(logger, "Successfully retrieved user", mapOf("accountId" to query.accountId))
            .logOnError(logger, "Failed to retrieve user", mapOf("accountId" to query.accountId))
    }
}