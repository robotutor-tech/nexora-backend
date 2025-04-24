package com.robotutor.nexora.user.services

import com.robotutor.iot.exceptions.DuplicateDataException
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.user.conrollers.views.UserRequest
import com.robotutor.nexora.user.exceptions.NexoraError
import com.robotutor.nexora.user.models.IdType
import com.robotutor.nexora.user.models.UserDetails
import com.robotutor.nexora.user.repositories.UserRepository
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.services.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService(val userRepository: UserRepository, val idGeneratorService: IdGeneratorService) {

    val logger = Logger(this::class.java)

    fun register(userRequest: UserRequest): Mono<UserDetails> {
        return userRepository.existsByEmail(userRequest.email)
            .flatMap { existsByEmail ->
                if (!existsByEmail)
                    idGeneratorService.generateId(IdType.USER_ID)
                        .flatMap { userId ->
                            userRepository.save(UserDetails.from(userId, userRequest))
                        }
                        .logOnSuccess(logger, "Successfully registered user")
                else {
                    createMonoError(DuplicateDataException(NexoraError.NEXORA0201))
                }
            }
            .logOnError(logger, "", "Failed to registered user")
    }
}
