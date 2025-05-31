package com.robotutor.nexora.auth.services

import com.robotutor.nexora.auth.controllers.views.AuthLoginRequest
import com.robotutor.nexora.auth.controllers.views.AuthUserRequest
import com.robotutor.nexora.auth.exceptions.NexoraError
import com.robotutor.nexora.auth.models.AuthUser
import com.robotutor.nexora.auth.repositories.AuthRepository
import com.robotutor.nexora.kafka.auditOnSuccess
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.models.ActorIdentifier
import com.robotutor.nexora.security.models.Identifier
import com.robotutor.nexora.security.models.UserId
import com.robotutor.nexora.webClient.exceptions.BadDataException
import com.robotutor.nexora.webClient.exceptions.DuplicateDataException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class AuthService(
    val authRepository: AuthRepository,
    val passwordEncoder: PasswordEncoder
) {
    val logger = Logger(this::class.java)

    fun register(authUserRequest: AuthUserRequest): Mono<AuthUser> {
        return authRepository.existsByUserId(authUserRequest.userId)
            .flatMap {
                if (it) {
                    createMonoError(DuplicateDataException(NexoraError.NEXORA0201))
                } else {
                    val password = passwordEncoder.encode(authUserRequest.password)
                    val authUser = AuthUser.from(authUserRequest, password)
                    authRepository.save(authUser)
                        .logOnSuccess(logger, "Successfully registered user ${authUserRequest.userId}")
                        .auditOnSuccess(
                            "AUTH_USER_CREATED", identifier = Identifier(
                                authUserRequest.userId,
                                ActorIdentifier.USER
                            )
                        )
                }
            }
            .logOnError(logger, "", "Successfully registered user ${authUserRequest.userId}")
    }

    fun login(authLoginRequest: AuthLoginRequest): Mono<AuthUser> {
        return authRepository.findByEmail(authLoginRequest.email)
            .switchIfEmpty { createMonoError(BadDataException(NexoraError.NEXORA0202)) }
            .flatMap {
                val matches = passwordEncoder.matches(authLoginRequest.password, it.password)
                if (!matches) {
                    createMonoError<AuthUser>(BadDataException(NexoraError.NEXORA0202))
                        .logOnError(
                            logger,
                            NexoraError.NEXORA0202.errorCode,
                            "Failed to login user ${authLoginRequest.email} due to ${NexoraError.NEXORA0202}"
                        )
                } else {
                    createMono(it)
                        .auditOnSuccess("USER_LOGIN", identifier = Identifier(it.userId, ActorIdentifier.USER))
                }
            }
            .logOnSuccess(logger, "Successfully logged in user ${authLoginRequest.email}")
    }

    fun getAuthUser(userId: UserId): Mono<AuthUser> {
        return authRepository.findByUserId(userId)
    }

}
