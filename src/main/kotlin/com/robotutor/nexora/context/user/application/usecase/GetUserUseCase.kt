package com.robotutor.nexora.context.user.application.usecase

import com.robotutor.nexora.shared.utility.createMonoError
import com.robotutor.nexora.context.user.application.command.GetUserQuery
import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.domain.exception.UserError
import com.robotutor.nexora.context.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class GetUserUseCase(private val userRepository: UserRepository) {
    val logger = Logger(this::class.java)

    fun execute(query: GetUserQuery): Mono<UserAggregate> {
        return userRepository.findByAccountId(query.accountId)
            .switchIfEmpty(createMonoError(DataNotFoundException(UserError.NEXORA0205)))
            .logOnSuccess(logger, "Successfully retrieved user", mapOf("accountId" to query.accountId))
            .logOnError(logger, "Failed to retrieve user", mapOf("accountId" to query.accountId))
    }
}