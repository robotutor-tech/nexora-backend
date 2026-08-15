package com.robotutor.nexora.module.identity.interfaces.controller.mapper

import com.robotutor.nexora.module.identity.application.command.ValidateTokenCommand
import com.robotutor.nexora.module.identity.application.view.SessionValidationResult
import com.robotutor.nexora.module.identity.interfaces.controller.view.*
import com.robotutor.nexora.shared.domain.vo.AccessToken
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.Tokens
import com.robotutor.nexora.shared.domain.vo.PrincipalData
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.DeviceData
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
            principal = toSessionPrincipalResponse(sessionValidationResult.principalData),
        )
    }

    private fun toSessionPrincipalResponse(principal: PrincipalData): PrincipalDataResponse {
        return when (principal) {
            is ActorData -> ActorPrincipalResponse(
                principal.actorId.value,
                principal.premisesId.value,
                toAccountPrincipalResponse(principal.accountData)
            )

            is AccountData -> toAccountPrincipalResponse(principal)
        }
    }

    private fun toAccountPrincipalResponse(accountData: AccountData): AccountPrincipalResponse {
        return when (accountData) {
            is DeviceData -> DevicePrincipalResponse(accountData.accountId.value, accountData.principalId.value)
            is UserData -> UserPrincipalResponse(accountData.userId.value, accountData.principalId.value)
        }
    }
}
