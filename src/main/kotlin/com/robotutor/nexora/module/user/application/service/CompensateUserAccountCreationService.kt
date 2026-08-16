package com.robotutor.nexora.module.user.application.service

import com.robotutor.nexora.module.user.application.command.CompensateUserAccountCreationCommand
import com.robotutor.nexora.module.user.domain.aggregate.User
import com.robotutor.nexora.module.user.domain.event.UserAccountCreationCompensatedEvent
import com.robotutor.nexora.module.user.domain.event.UserEvent
import com.robotutor.nexora.module.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.domain.vo.UserData
import com.robotutor.nexora.shared.message.mapper.EventMapper
import com.robotutor.nexora.shared.outbox.auditOnSuccess
import com.robotutor.nexora.shared.outbox.publishEvent
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CompensateUserAccountCreationService(
    private val userRepository: UserRepository,
    private val eventMapper: EventMapper<UserEvent>,
) {
    private val logger = Logger(this::class.java)

    fun execute(command: CompensateUserAccountCreationCommand): Mono<User> {
        return userRepository.deleteByUserId(command.userId)
            .auditOnSuccess(
                "USER_ACCOUNT_CREATION_COMPENSATE",
                ResourceType.USER,
                command.userId,
                principal = UserData(command.userId, AccountId("UNKNOWN_ACCOUNT_ID"))
            )
            .publishEvent(UserAccountCreationCompensatedEvent(command.userId), eventMapper)
            .logOnSuccess(logger, "Successfully compensated user account creation", mapOf("userId" to command.userId))
            .logOnError(logger, "Failed to compensate user account creation", mapOf("userId" to command.userId))
    }
}
