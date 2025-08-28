package com.robotutor.nexora.modules.auth.application

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.modules.auth.application.command.LoginCommand
import com.robotutor.nexora.modules.auth.application.command.RegisterAuthUserCommand
import com.robotutor.nexora.modules.auth.application.dto.AuthUserResponse
import com.robotutor.nexora.modules.auth.application.dto.TokenResponses
import com.robotutor.nexora.modules.auth.domain.exception.NexoraError
import com.robotutor.nexora.modules.auth.domain.model.AuthUser
import com.robotutor.nexora.modules.auth.domain.repository.AuthUserRepository
import com.robotutor.nexora.modules.auth.domain.service.PasswordService
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.exception.BadDataException
import com.robotutor.nexora.shared.domain.exception.DuplicateDataException
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
    private val authUserRepository: AuthUserRepository,
    private val passwordService: PasswordService,
    private val tokenUseCase: TokenUseCase
) {
    private val logger = Logger(this::class.java)

    fun register(registerAuthUserCommand: RegisterAuthUserCommand): Mono<AuthUserResponse> {
        return authUserRepository.findByEmail(registerAuthUserCommand.email)
            .flatMap { createMonoError<AuthUser>(DuplicateDataException(NexoraError.NEXORA0201)) }
            .switchIfEmpty(registerAuthUser(registerAuthUserCommand))
            .publishEvents()
            .logOnSuccess(logger, "Successfully registered auth user for ${registerAuthUserCommand.userId}")
            .logOnError(logger, "", "Failed to register auth user ${registerAuthUserCommand.userId}")
            .map { AuthUserResponse.from(it) }
    }

    private fun registerAuthUser(command: RegisterAuthUserCommand): Mono<AuthUser> {
        val authUser = AuthUser.register(
            userId = command.userId,
            email = command.email,
            password = passwordService.encodePassword(command.password),
        )
        return authUserRepository.save(authUser).map { authUser }
    }

    fun login(loginCommand: LoginCommand): Mono<TokenResponses> {
        return validateCredentials(loginCommand)
            .flatMap { authUser ->
                tokenUseCase.generateTokenWithRefreshToken(
                    TokenPrincipalType.USER,
                    UserContext(authUser.userId)
                )
            }
    }

    private fun validateCredentials(loginCommand: LoginCommand): Mono<AuthUser> {
        return authUserRepository.findByEmail(loginCommand.email)
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