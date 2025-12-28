package com.robotutor.nexora.context.user.application.usecase

import com.robotutor.nexora.context.user.application.command.RegisterUserCommand
import com.robotutor.nexora.context.user.application.policy.RegisterUserPolicy
import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.domain.exception.UserError
import com.robotutor.nexora.context.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.utility.errorOnDenied
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterUserUseCase(
    private val registerUserPolicy: RegisterUserPolicy,
    private val userRepository: UserRepository,
    
) {
    private val logger = Logger(this::class.java)

    fun execute(command: RegisterUserCommand): Mono<UserAggregate> {
        return registerUserPolicy.evaluate(command)
            .errorOnDenied(UserError.NEXORA0201)
            .map { UserAggregate.register(name = command.name, email = command.email, mobile = command.mobile) }
            .flatMap { user -> userRepository.save(user) }
            .logOnSuccess(logger, "Successfully registered user")
            .logOnError(logger, "Failed to registered user")
    }
}