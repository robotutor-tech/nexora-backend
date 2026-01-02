package com.robotutor.nexora.context.user.application.service

import com.robotutor.nexora.context.user.application.command.ActivateUserCommand
import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActivateUserService(
    private val userRepository: UserRepository,
    
) {
    private val logger = Logger(this::class.java)

    fun execute(command: ActivateUserCommand): Mono<UserAggregate> {
        return userRepository.findByUserId(command.userId)
            .map { user -> user.activate() }
            .flatMap { user -> userRepository.save(user) }
            .logOnSuccess(logger, "Successfully registered user")
            .logOnError(logger, "Failed to registered user")
    }
}