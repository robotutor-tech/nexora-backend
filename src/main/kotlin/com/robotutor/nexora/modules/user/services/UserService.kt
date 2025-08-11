//package com.robotutor.nexora.modules.user.services
//
//import com.robotutor.nexora.kafka.auditOnError
//import com.robotutor.nexora.kafka.auditOnSuccess
//import com.robotutor.nexora.webClient.exceptions.DuplicateDataException
//import com.robotutor.nexora.logger.Logger
//import com.robotutor.nexora.logger.logOnError
//import com.robotutor.nexora.logger.logOnSuccess
//import com.robotutor.nexora.user.adapters.conroller.dto.UserRequest
//import com.robotutor.nexora.user.exceptions.NexoraError
//import com.robotutor.nexora.user.domain.model.IdType
//import com.robotutor.nexora.user.domain.model.UserDetails
//import com.robotutor.nexora.user.repositories.UserRepository
//import com.robotutor.nexora.common.security.createMonoError
//import com.robotutor.nexora.common.security.models.ActorIdentifier
//import com.robotutor.nexora.common.security.models.Identifier
//import com.robotutor.nexora.common.security.models.UserId
//import com.robotutor.nexora.common.security.services.IdGeneratorService
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Mono
//
//@Service
//class UserService(val userRepository: UserRepository, val idGeneratorService: IdGeneratorService) {
//
//    val logger = Logger(this::class.java)
//
//    fun register(userRequest: UserRequest): Mono<UserDetails> {
//        return userRepository.existsByEmail(userRequest.email)
//            .flatMap { existsByEmail ->
//                if (!existsByEmail)
//                    idGeneratorService.generateId(IdType.USER_ID)
//                        .flatMap { userId ->
//                            userRepository.save(UserDetails.from(userId, userRequest))
//                                .auditOnSuccess(
//                                    "USER_CREATED",
//                                    mapOf("email" to userRequest.email),
//                                    Identifier(userId, ActorIdentifier.USER)
//                                )
//                        }
//                        .logOnSuccess(logger, "Successfully registered user")
//                else {
//                    createMonoError(DuplicateDataException(NexoraError.NEXORA0201))
//                }
//            }
//            .logOnError(logger, "", "Failed to registered user")
//    }
//
//    fun deleteUserByUserId(userId: UserId): Mono<UserDetails> {
//        return userRepository.deleteByUserId(userId)
//            .auditOnSuccess("USER_DELETED", identifier = Identifier(userId, ActorIdentifier.USER))
//            .auditOnError("USER_DELETED", identifier = Identifier(userId, ActorIdentifier.USER))
//            .logOnSuccess(logger, "Successfully deleted user", mapOf("userId" to userId))
//            .logOnError(logger, "", "Failed to delete user", mapOf("userId" to userId))
//    }
//}
