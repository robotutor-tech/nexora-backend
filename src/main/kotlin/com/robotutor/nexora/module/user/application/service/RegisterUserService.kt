package com.robotutor.nexora.module.user.application.service

import com.robotutor.nexora.module.user.application.command.RegisterUserCommand
import com.robotutor.nexora.module.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.module.user.domain.exception.UserError
import com.robotutor.nexora.module.user.domain.policy.RegisterUserPolicy
import com.robotutor.nexora.module.user.domain.policy.context.DuplicateUserContext
import com.robotutor.nexora.module.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.utility.enforcePolicy
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterUserService(
    private val registerUserPolicy: RegisterUserPolicy,
    private val userRepository: UserRepository,
) {
    private val logger = Logger(this::class.java)

    fun execute(command: RegisterUserCommand): Mono<UserAggregate> {
        return userRepository.existsByEmail(command.email)
            .enforcePolicy(registerUserPolicy, { DuplicateUserContext(it, command.email) }, UserError.NEXORA0201)
            .map { UserAggregate.register(name = command.name, email = command.email, mobile = command.mobile) }
            .flatMap { user -> userRepository.save(user) }
            .logOnSuccess(logger, "Successfully registered user")
            .logOnError(logger, "Failed to registered user")
    }
}