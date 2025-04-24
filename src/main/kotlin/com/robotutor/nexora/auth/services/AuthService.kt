package com.robotutor.nexora.auth.services

import com.robotutor.iot.exceptions.BadDataException
import com.robotutor.iot.exceptions.DuplicateDataException
import com.robotutor.nexora.auth.controllers.views.AuthLoginRequest
import com.robotutor.nexora.auth.controllers.views.AuthUserRequest
import com.robotutor.nexora.auth.exceptions.NexoraError
import com.robotutor.nexora.auth.models.AuthUser
import com.robotutor.nexora.auth.models.Token
import com.robotutor.nexora.auth.repositories.AuthRepository
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.createMonoError
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class AuthService(
    val authRepository: AuthRepository,
    val tokenService: TokenService,
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
                }
            }
            .logOnError(logger, "", "Successfully registered user ${authUserRequest.userId}")
    }

    fun login(authLoginRequest: AuthLoginRequest): Mono<Token> {
        return authRepository.findByEmail(authLoginRequest.email)
            .flatMap {
                val matches = passwordEncoder.matches(authLoginRequest.password, it.password)
                if (!matches) {
                    createMonoError(BadDataException(NexoraError.NEXORA0202))
                } else {
                    tokenService.generateToken(it)
                }
            }
            .switchIfEmpty {
                createMonoError(BadDataException(NexoraError.NEXORA0202))
            }
    }

}
