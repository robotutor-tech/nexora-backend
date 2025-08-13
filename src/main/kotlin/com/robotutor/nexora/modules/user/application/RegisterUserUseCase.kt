package com.robotutor.nexora.modules.user.application

import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.modules.user.application.command.RegisterAuthUserCommand
import com.robotutor.nexora.modules.user.application.command.RegisterUserCommand
import com.robotutor.nexora.modules.user.application.service.RegisterAuthUser
import com.robotutor.nexora.modules.user.domain.exception.NexoraError
import com.robotutor.nexora.modules.user.domain.model.Email
import com.robotutor.nexora.modules.user.domain.model.IdType
import com.robotutor.nexora.modules.user.domain.model.User
import com.robotutor.nexora.modules.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.adapters.webclient.exceptions.DuplicateDataException
import com.robotutor.nexora.shared.domain.model.UserId
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RegisterUserUseCase(
    private val userRepository: UserRepository,
    private val idGeneratorService: IdGeneratorService,
    private val registerAuthUser: RegisterAuthUser
) {
    val logger = Logger(this::class.java)

    fun register(registerUserCommand: RegisterUserCommand): Mono<User> {
        return userRepository.existsByEmail(Email(registerUserCommand.email))
            .flatMap { existsByEmail ->
                if (!existsByEmail)
                    registerUser(registerUserCommand)
                else {
                    createMonoError(DuplicateDataException(NexoraError.NEXORA0201))
                }
            }
            .flatMap { user -> registerAuthUser(user, registerUserCommand) }
            // TODO: Audit success at last
            .logOnSuccess(logger, "Successfully registered user")
            .logOnError(logger, "", "Failed to registered user")
    }

    private fun registerAuthUser(user: User, registerUserCommand: RegisterUserCommand): Mono<User> {
        val registerAuthUserCommand = RegisterAuthUserCommand(
            userId = user.userId,
            email = user.email,
            password = registerUserCommand.password,
        )
        return registerAuthUser.register(registerAuthUserCommand)
            .map { user }
            .onErrorResume { throwable ->
                userRepository.deleteByUserId(user.userId)
                    .flatMap { createMonoError(throwable) }
            }
    }

    private fun registerUser(registerUserCommand: RegisterUserCommand): Mono<User> {
        return idGeneratorService.generateId(IdType.USER_ID)
            .flatMap { userId ->
                val user = User(
                    userId = UserId(value = userId),
                    name = registerUserCommand.name,
                    email = Email(registerUserCommand.email),
                )
                userRepository.save(user)
            }
    }
}