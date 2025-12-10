package com.robotutor.nexora.context.iam.application.usecase

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.context.iam.application.command.ValidateTokenCommand
import com.robotutor.nexora.context.iam.application.view.SessionValidationResult
import com.robotutor.nexora.context.iam.domain.service.TokenGenerator
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ValidateSessionUseCase(
    private val tokenGenerator: TokenGenerator
) {
    fun execute(command: ValidateTokenCommand): Mono<SessionValidationResult> {
        val tokenPayload = tokenGenerator.validateAccessToken(command.tokenValue)
        return createMono(
            SessionValidationResult(
                isValid = true,
                sessionPrincipal = tokenPayload.sessionPrincipal,
                expiresAt = tokenPayload.expiresAt
            )
        )
    }
}