package com.robotutor.nexora.context.user.application.usecase

import com.robotutor.nexora.context.user.application.command.ActivateUserCommand
import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActivateUserUseCase(private val userRepository: UserRepository) {
    val logger = Logger(this::class.java)

    fun execute(command: ActivateUserCommand): Mono<UserAggregate> {
        return userRepository.findByUserId(command.userId)
            .map { user -> user.activate() }
            .flatMap { user -> userRepository.save(user) }
            .logOnSuccess(logger, "Successfully registered user")
            .logOnError(logger, "Failed to registered user")
    }
}