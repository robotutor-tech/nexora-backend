package com.robotutor.nexora.modules.user.application

import com.robotutor.nexora.shared.adapters.messaging.auditOnSuccess
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import com.robotutor.nexora.modules.user.domain.model.IdType
import com.robotutor.nexora.modules.user.domain.model.User
import com.robotutor.nexora.modules.user.domain.model.UserDetails
import com.robotutor.nexora.modules.user.domain.repository.UserRepository
import com.robotutor.nexora.modules.user.domain.exception.NexoraError
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.common.security.models.ActorIdentifier
import com.robotutor.nexora.common.security.models.Identifier
import com.robotutor.nexora.shared.domain.model.UserId
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.shared.adapters.outbound.webclient.exceptions.DuplicateDataException
import reactor.core.publisher.Mono

class UserUseCase(private val userRepository: UserRepository, private val idGeneratorService: IdGeneratorService) {
    val logger = Logger(this::class.java)

    fun register(userDetails: UserDetails): Mono<User> {
        return userRepository.existsByEmail(userDetails.email)
            .flatMap { existsByEmail ->
                if (!existsByEmail)
                    idGeneratorService.generateId(IdType.USER_ID)
                        .flatMap { userId ->
                            val user = User.from(UserId(userId), userDetails)
                            userRepository.save(user)
                                .auditOnSuccess(
                                    "USER_CREATED",
                                    mapOf("email" to userDetails.email),
                                    Identifier(userId, ActorIdentifier.USER)
                                )
                        }
                else {
                    createMonoError(DuplicateDataException(NexoraError.NEXORA0201))
                }
            }

            .logOnSuccess(logger, "Successfully registered user")
            .logOnError(logger, "", "Failed to registered user")
    }
}