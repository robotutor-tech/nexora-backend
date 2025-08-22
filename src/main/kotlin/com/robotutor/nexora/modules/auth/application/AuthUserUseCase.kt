package com.robotutor.nexora.modules.auth.application

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.modules.auth.application.command.LoginCommand
import com.robotutor.nexora.modules.auth.application.command.RegisterAuthUserCommand
import com.robotutor.nexora.modules.auth.application.dto.AuthUserResponse
import com.robotutor.nexora.modules.auth.application.dto.TokenResponses
import com.robotutor.nexora.modules.auth.domain.exception.NexoraError
import com.robotutor.nexora.modules.auth.domain.model.AuthUser
import com.robotutor.nexora.modules.auth.domain.repository.AuthRepository
import com.robotutor.nexora.modules.auth.domain.service.PasswordService
import com.robotutor.nexora.shared.adapters.webclient.exceptions.BadDataException
import com.robotutor.nexora.shared.adapters.webclient.exceptions.DuplicateDataException
import com.robotutor.nexora.shared.domain.model.TokenPrincipalType
import com.robotutor.nexora.shared.domain.model.UserContext
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class AuthUserUseCase(
    private val authRepository: AuthRepository,
    private val passwordService: PasswordService,
    private val tokenUseCase: TokenUseCase
) {
    private val logger = Logger(this::class.java)

    fun register(registerAuthUserCommand: RegisterAuthUserCommand): Mono<AuthUserResponse> {
        return authRepository.existsByUserId(registerAuthUserCommand.userId)
            .flatMap {
                if (it) {
                    createMonoError(DuplicateDataException(NexoraError.NEXORA0201))
                } else {
                    registerAuthUser(registerAuthUserCommand)
                }
            }
            .logOnSuccess(logger, "Successfully registered auth user for ${registerAuthUserCommand.userId}")
            .logOnError(logger, "", "Failed to register auth user ${registerAuthUserCommand.userId}")
            .map { AuthUserResponse.from(it) }
        // TODO: Audit for success or failure
    }

    private fun registerAuthUser(registerAuthUserCommand: RegisterAuthUserCommand): Mono<AuthUser> {
        val authUser = AuthUser(
            userId = registerAuthUserCommand.userId,
            email = registerAuthUserCommand.email,
            password = passwordService.encodePassword(registerAuthUserCommand.password),
        )
        return authRepository.save(authUser)
    }

    fun login(loginCommand: LoginCommand): Mono<TokenResponses> {
        return validateCredentials(loginCommand)
            .flatMap { authUser ->
                tokenUseCase.generateTokenWithRefreshToken(
                    TokenPrincipalType.USER,
                    UserContext(authUser.userId)
                )
            }
            .map { TokenResponses.from(it) }
    }

    private fun validateCredentials(loginCommand: LoginCommand): Mono<AuthUser> {
        return authRepository.findByEmail(loginCommand.email)
            .switchIfEmpty { createMonoError(BadDataException(NexoraError.NEXORA0202)) }
            .flatMap { authUser ->
                val matches = passwordService.matches(loginCommand.password, authUser.password)
                if (!matches) {
                    createMonoError(BadDataException(NexoraError.NEXORA0202))
                } else {
                    createMono(authUser)
                }
            }
            .logOnSuccess(logger, "Successfully validated credentials for ${loginCommand.email}")
            .logOnError(logger, "", "Failed to validate credentials for ${loginCommand.email}")
    }
}