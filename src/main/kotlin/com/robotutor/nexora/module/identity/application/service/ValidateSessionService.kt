package com.robotutor.nexora.module.identity.application.service

import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.module.identity.application.command.ValidateTokenCommand
import com.robotutor.nexora.module.identity.application.view.SessionValidationResult
import com.robotutor.nexora.module.identity.domain.service.TokenGenerator
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ValidateSessionService(private val tokenGenerator: TokenGenerator) {
    fun execute(command: ValidateTokenCommand): Mono<SessionValidationResult> {
        val session = tokenGenerator.getSession(command.token)
        return createMono(
            SessionValidationResult(
                isValid = true,
                principalData = session.principalData,
                expiresAt = session.expiresAt
            )
        )
    }
}
