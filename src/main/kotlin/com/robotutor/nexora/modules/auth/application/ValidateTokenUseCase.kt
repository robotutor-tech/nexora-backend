package com.robotutor.nexora.modules.auth.application

import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.modules.auth.application.command.ValidateTokenCommand
import com.robotutor.nexora.modules.auth.application.dto.TokenValidationResult
import com.robotutor.nexora.modules.auth.domain.exception.NexoraError
import com.robotutor.nexora.modules.auth.domain.repository.TokenRepository
import com.robotutor.nexora.shared.adapters.webclient.exceptions.UnAuthorizedException
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ValidateTokenUseCase(
    private val tokenRepository: TokenRepository,
) {
    private val logger = Logger(this::class.java)

    fun validate(validateTokenCommand: ValidateTokenCommand): Mono<TokenValidationResult> {
        return tokenRepository.findByValue(validateTokenCommand.token)
            .switchIfEmpty(
                createMonoError(UnAuthorizedException(NexoraError.NEXORA0206))
            )
            .map {
                TokenValidationResult(
                    isValid = true,
                    principalId = it.identifier.id,
                    principalType = it.identifier.type,
                    expiresAt = it.expiresAt,
                )
            }
            .logOnSuccess(logger, "Successfully validated token")
            .logOnError(logger, "", "Failed to validate token")
    }
}