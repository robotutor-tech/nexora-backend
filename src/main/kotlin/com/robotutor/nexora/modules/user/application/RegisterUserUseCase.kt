package com.robotutor.nexora.modules.user.application

import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.modules.user.application.command.RegisterAuthUserCommand
import com.robotutor.nexora.modules.user.application.command.RegisterUserCommand
import com.robotutor.nexora.modules.user.application.service.RegisterAuthUser
import com.robotutor.nexora.modules.user.domain.exception.NexoraError
import com.robotutor.nexora.modules.user.domain.model.IdType
import com.robotutor.nexora.modules.user.domain.model.User
import com.robotutor.nexora.modules.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.exception.DuplicateDataException
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
        return userRepository
                .findByEmail(registerUserCommand.email)
                .flatMap { createMonoError<User>(DuplicateDataException(NexoraError.NEXORA0201)) }
                .switchIfEmpty(registerUser(registerUserCommand))
                .flatMap { user -> registerAuthUser(user, registerUserCommand) }
//                .publishEvents()
                .logOnSuccess(logger, "Successfully registered user")
                .logOnError(logger, "", "Failed to registered user")
    }

    private fun registerAuthUser(user: User, command: RegisterUserCommand): Mono<User> {
        val registerAuthUserCommand =
                RegisterAuthUserCommand(
                        userId = user.userId,
                        email = user.email,
                        password = command.password
                )
        return registerAuthUser.register(registerAuthUserCommand).map { user }.onErrorResume {
                throwable ->
            userRepository.deleteByUserId(user.userId).map { user.clearDomainEvents() }.flatMap {
                createMonoError(throwable)
            }
        }
    }

    private fun registerUser(command: RegisterUserCommand): Mono<User> {
        return idGeneratorService
                .generateId(IdType.USER_ID, UserId::class.java)
                .map { userId ->
                    User.register(userId = userId, name = command.name, email = command.email, mobile = command.mobile)
                }
                .flatMap { user -> userRepository.save(user).map { user } }
    }
}
