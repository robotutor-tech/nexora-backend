package com.robotutor.nexora.module.user.application.service

import com.robotutor.nexora.module.user.application.command.ActivateUserCommand
import com.robotutor.nexora.module.user.domain.aggregate.User
import com.robotutor.nexora.module.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.domain.vo.UserData
import com.robotutor.nexora.shared.outbox.auditOnSuccess
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class ActivateUserService(
    private val userRepository: UserRepository,

    ) {
    private val logger = Logger(this::class.java)

    @Transactional
    fun execute(command: ActivateUserCommand): Mono<User> {
        return userRepository.findByUserId(command.userId)
            .map { user -> user.activate() }
            .flatMap { user ->
                userRepository.save(user)
                    .auditOnSuccess(
                        "USER_ACTIVATED",
                        ResourceType.USER,
                        command.userId,
                        principal = UserData(command.userId, command.accountId)
                    )
            }
            .logOnSuccess(logger, "Successfully activated user")
            .logOnError(logger, "Failed to activate user")
    }
}
