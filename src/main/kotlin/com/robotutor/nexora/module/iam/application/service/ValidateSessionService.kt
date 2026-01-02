package com.robotutor.nexora.module.iam.application.service

import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.module.iam.application.command.ValidateTokenCommand
import com.robotutor.nexora.module.iam.application.view.SessionValidationResult
import com.robotutor.nexora.module.iam.domain.service.TokenGenerator
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ValidateSessionService(private val tokenGenerator: TokenGenerator) {
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