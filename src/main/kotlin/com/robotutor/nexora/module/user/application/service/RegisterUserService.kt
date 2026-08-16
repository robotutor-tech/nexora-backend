package com.robotutor.nexora.module.user.application.service

import com.robotutor.nexora.module.user.application.command.RegisterUserCommand
import com.robotutor.nexora.module.user.domain.aggregate.User
import com.robotutor.nexora.module.user.domain.exception.UserError
import com.robotutor.nexora.module.user.domain.policy.RegisterUserPolicy
import com.robotutor.nexora.module.user.domain.policy.context.DuplicateUserContext
import com.robotutor.nexora.module.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.utility.enforcePolicy
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.domain.vo.UserData
import com.robotutor.nexora.shared.outbox.auditOnSuccess
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class RegisterUserService(
    private val registerUserPolicy: RegisterUserPolicy,
    private val userRepository: UserRepository,
) {
    private val logger = Logger(this::class.java)

    @Transactional
    fun execute(command: RegisterUserCommand): Mono<User> {
        return userRepository.existsByEmail(command.email)
            .enforcePolicy(registerUserPolicy, UserError.NEXORA0201) {
                DuplicateUserContext(it, command.email)
            }
            .map { User.register(name = command.name, email = command.email, mobile = command.mobile) }
            .flatMap { user ->
                userRepository.save(user)
                    .auditOnSuccess(
                        "USER_REGISTERED",
                        ResourceType.USER,
                        user.userId,
                        command.toMetaData(),
                        UserData(user.userId, AccountId("UNKNOWN"))
                    )
                    .logOnSuccess(logger, "Successfully registered user")
            }
            .logOnError(logger, "Failed to registered user")
    }
}
