package com.robotutor.nexora.context.user.application.usecase

import com.robotutor.nexora.context.user.application.command.CompensateUserRegistrationCommand
import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.domain.event.UserEvent
import com.robotutor.nexora.context.user.domain.event.UserRegistrationCompensatedEvent
import com.robotutor.nexora.context.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CompensateUserRegistrationUseCase(
    private val userRepository: UserRepository,
    private val eventPublisher: EventPublisher<UserEvent>
) {
    private val logger = Logger(this::class.java)

    fun execute(command: CompensateUserRegistrationCommand): Mono<UserAggregate> {
        return userRepository.deleteByUserId(command.userId)
            .publishEvent(eventPublisher, UserRegistrationCompensatedEvent(command.userId))
            .logOnSuccess(logger, "Successfully compensate user registration", mapOf("userId" to command.userId))
            .logOnError(logger, "", "Failed to compensate user registration", mapOf("userId" to command.userId))
    }
}