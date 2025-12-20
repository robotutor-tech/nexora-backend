package com.robotutor.nexora.context.user.application.usecase

import com.robotutor.nexora.context.user.application.command.RegisterUserCommand
import com.robotutor.nexora.context.user.application.policy.RegisterUserPolicy
import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.domain.exception.NexoraError
import com.robotutor.nexora.context.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.infrastructure.utility.errorOnDenied
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterUserUseCase(
    private val registerUserPolicy: RegisterUserPolicy,
    private val userRepository: UserRepository,
) {
    val logger = Logger(this::class.java)

    fun execute(command: RegisterUserCommand): Mono<UserAggregate> {
        return registerUserPolicy.evaluate(command)
            .errorOnDenied(NexoraError.NEXORA0201)
            .map {
                UserAggregate.register(
                    accountId = command.accountId,
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