package com.robotutor.nexora.context.user.application.service

import com.robotutor.nexora.context.user.application.command.GetUserQuery
import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.domain.exception.UserError
import com.robotutor.nexora.context.user.domain.repository.UserRepository
import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.shared.application.cache.CacheNames
import com.robotutor.nexora.shared.application.cache.annotation.Cached
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.utility.createMonoError
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class GetUserService(
    private val userRepository: UserRepository,
    
) {
    private val logger = Logger(this::class.java)

    @Cached(
        cacheName = CacheNames.USER_BY_ID,
        key = "T(com.robotutor.nexora.shared.application.cache.CacheKeys).userById(#query.principalId.value)",
    )
    fun execute(query: GetUserQuery): Mono<UserAggregate> {
        return userRepository.findByUserId(UserId(query.principalId.value))
            .switchIfEmpty(createMonoError(DataNotFoundException(UserError.NEXORA0205)))
            .logOnSuccess(logger, "Successfully retrieved user", mapOf("principalId" to query.principalId))
            .logOnError(logger, "Failed to retrieve user", mapOf("principalId" to query.principalId))
    }
}