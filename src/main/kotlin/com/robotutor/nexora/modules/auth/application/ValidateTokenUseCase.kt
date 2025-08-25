package com.robotutor.nexora.modules.auth.application

import com.robotutor.nexora.modules.auth.application.command.ValidateTokenCommand
import com.robotutor.nexora.modules.auth.application.dto.TokenValidationResult
import com.robotutor.nexora.shared.interfaces.mapper.PrincipalContextMapper
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ValidateTokenUseCase(
    private val tokenUseCase: TokenUseCase,
) {
    private val logger = Logger(this::class.java)

    fun validate(validateTokenCommand: ValidateTokenCommand): Mono<TokenValidationResult> {
        return tokenUseCase.findTokenByValue(validateTokenCommand.token)
            .map {
                TokenValidationResult(
                    isValid = true,
                    principal = PrincipalContextMapper.toPrincipalContextResponse(it.principal),
                    principalType = it.principalType,
                    expiresAt = it.expiresAt
                )
            }
            .logOnSuccess(logger, "Successfully validated token")
            .logOnError(logger, "", "Failed to validate token")
    }
}