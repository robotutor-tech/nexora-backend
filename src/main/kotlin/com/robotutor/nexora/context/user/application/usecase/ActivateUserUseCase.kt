package com.robotutor.nexora.context.user.application.usecase

import com.robotutor.nexora.context.user.application.command.ActivateUserCommand
import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.domain.event.UserEvent
import com.robotutor.nexora.context.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActivateUserUseCase(
    private val userRepository: UserRepository,
    private val eventPublisher: EventPublisher<UserEvent>
) {
    private val logger = Logger(this::class.java)

    fun execute(command: ActivateUserCommand): Mono<UserAggregate> {
        return userRepository.findByEmail(command.email)
            .map { user -> user.activate(command.accountId) }
            .flatMap { user -> userRepository.save(user).map { user } }
            .publishEvents(eventPublisher)
            .logOnSuccess(logger, "Successfully activated user", mapOf("accountId" to command.accountId))
            .logOnError(logger, "Failed to activate user", mapOf("accountId" to command.accountId))
    }
}