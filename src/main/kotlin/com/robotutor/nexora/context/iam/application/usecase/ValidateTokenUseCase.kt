package com.robotutor.nexora.context.iam.application.usecase

import com.robotutor.nexora.context.iam.application.command.ValidateTokenCommand
import com.robotutor.nexora.context.iam.application.view.TokenValidationResult
import com.robotutor.nexora.context.iam.domain.repository.TokenRepository
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class ValidateTokenUseCase(
    private val tokenRepository: TokenRepository,
) {
    private val logger = Logger(this::class.java)

    fun execute(command: ValidateTokenCommand): Mono<TokenValidationResult> {
        return tokenRepository.findByValueAndExpiredAtAfter(command.tokenValue, Instant.now())
            .map {
                TokenValidationResult(
                    isValid = true,
                    principal = it.principal,
                    principalType = it.principalType,
                    expiresAt = it.expiresAt
                )
            }
            .logOnSuccess(logger, "Successfully validated token")
            .logOnError(logger, "", "Failed to validate token")
    }
}