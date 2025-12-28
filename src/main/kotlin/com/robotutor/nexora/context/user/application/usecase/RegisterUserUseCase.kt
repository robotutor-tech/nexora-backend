package com.robotutor.nexora.context.user.application.usecase

import com.robotutor.nexora.context.user.application.command.RegisterUserCommand
import com.robotutor.nexora.context.user.application.policy.RegisterUserPolicy
import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.domain.exception.UserError
import com.robotutor.nexora.context.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.application.cache.CacheNames
import com.robotutor.nexora.shared.application.observability.AppLoggerFactory
import com.robotutor.nexora.shared.application.observability.logOnError
import com.robotutor.nexora.shared.application.observability.logOnSuccess
import com.robotutor.nexora.shared.domain.utility.errorOnDenied
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterUserUseCase(
    private val registerUserPolicy: RegisterUserPolicy,
    private val userRepository: UserRepository,
    loggerFactory: AppLoggerFactory,
) {
    private val logger = loggerFactory.forClass(this::class.java)

    @CacheEvict(
        cacheNames = [CacheNames.USER_BY_ID],
        allEntries = true,
    )
    fun execute(command: RegisterUserCommand): Mono<UserAggregate> {
        return registerUserPolicy.evaluate(command)
            .errorOnDenied(UserError.NEXORA0201)
            .map {
                UserAggregate.register(
                    name = command.name,
                    email = command.email,
                    mobile = command.mobile
                )
            }
            .flatMap { user -> userRepository.save(user) }
            .logOnSuccess(logger, "Successfully registered user")
            .logOnError(logger, "Failed to registered user")
    }
}