package com.robotutor.nexora.module.identity.interfaces.controller.mapper

import com.robotutor.nexora.module.identity.application.command.ValidateTokenCommand
import com.robotutor.nexora.module.identity.application.view.SessionValidationResult
import com.robotutor.nexora.module.identity.interfaces.controller.view.*
import com.robotutor.nexora.shared.domain.vo.AccessToken
import com.robotutor.nexora.shared.domain.vo.Tokens
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.UserData
import java.time.Instant

object SessionMapper {
    fun toTokenResponses(tokens: Tokens): TokenResponses {
        return TokenResponses(
            token = "Bearer ".plus(tokens.accessToken.value),
            refreshToken = "Bearer ".plus(tokens.refreshToken.value),
        )
    }

    fun toValidateSessionCommand(token: String): ValidateTokenCommand {
        return ValidateTokenCommand(AccessToken(token.removePrefix("Bearer ")))
    }

    fun toValidateSessionResponse(sessionValidationResult: SessionValidationResult): SessionValidateResponse {
        return SessionValidateResponse(
            isValid = sessionValidationResult.isValid,
            expiresIn = sessionValidationResult.expiresAt.epochSecond - Instant.now().epochSecond,
            principal = toSessionPrincipalResponse(sessionValidationResult.accountData),
        )
    }

    private fun toSessionPrincipalResponse(principal: AccountData): AccountDataResponse {
        return when (principal) {
            is UserData -> AccountPrincipalResponse(
                principal.accountId.value,
                principal.subjectType,
                principal.subjectId.value
            )

            is ActorData -> ActorPrincipalResponse(
                principal.actorId.value,
                principal.premisesId.value,
                principal.accountId.value,
                principal.subjectType,
                principal.subjectId.value
            )

            else -> throw IllegalArgumentException("Illegal principal $principal")
        }
    }
}
