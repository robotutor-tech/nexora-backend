package com.robotutor.nexora.common.security.adapters.client

import com.robotutor.nexora.common.security.application.ports.TokenValidator
import com.robotutor.nexora.common.security.domain.model.ValidateTokenResult
import com.robotutor.nexora.modules.auth.interfaces.controller.AuthController
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service("SecurityAuthValidatorClient")
class AuthTokenValidator(private val authController: AuthController) : TokenValidator {
    override fun validate(token: String): Mono<ValidateTokenResult> {
        return authController.validate(token)
            .map {
                ValidateTokenResult(
                    isValid = it.isValid,
                    principalId = it.principalId,
                    principalType = it.principalType,
                    expiresAt = it.expiresAt,
                )
            }
    }
}