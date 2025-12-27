package com.robotutor.nexora.context.user.application.usecase

import com.robotutor.nexora.context.user.application.command.CompensateUserCommand
import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.domain.event.UserCompensatedEvent
import com.robotutor.nexora.context.user.domain.event.UserEventPublisher
import com.robotutor.nexora.context.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.domain.event.publishEvent
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CompensateUserUseCase(
    private val userRepository: UserRepository,
    private val eventPublisher: UserEventPublisher
) {
    val logger = Logger(this::class.java)

    fun execute(command: CompensateUserCommand): Mono<UserAggregate> {
        return userRepository.deleteByUserId(command.userId)
            .publishEvent(eventPublisher, UserCompensatedEvent(command.userId))
            .logOnSuccess(logger, "Successfully compensated user", mapOf("userId" to command.userId))
            .logOnError(logger, "Failed to compensate user", mapOf("userId" to command.userId))
    }
}