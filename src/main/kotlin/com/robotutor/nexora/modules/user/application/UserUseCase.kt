package com.robotutor.nexora.modules.user.application

import com.robotutor.nexora.modules.user.application.command.GetUserCommand
import com.robotutor.nexora.modules.user.domain.model.User
import com.robotutor.nexora.modules.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserUseCase(
    private val userRepository: UserRepository,
) {
    val logger = Logger(this::class.java)

    fun getUser(getUserCommand: GetUserCommand): Mono<User> {
        return userRepository.findByUserId(getUserCommand.userId)
            .logOnSuccess(logger, "Successfully retrieved user")
            .logOnError(logger, "", "Failed to retrieve user")
    }
}