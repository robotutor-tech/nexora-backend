package com.robotutor.nexora.context.iam.interfaces.controller.view

import com.robotutor.nexora.context.iam.domain.aggregate.AccountType
import com.robotutor.nexora.context.iam.domain.aggregate.TokenPrincipalType

data class TokenResponses(val token: String, val refreshToken: String)
data class TokenValidationResponse(
    val isValid: Boolean,
    val principalType: TokenPrincipalType,
    val principal: TokenPrincipalContextResponse,
    val expiresIn: Number,
)

sealed interface TokenPrincipalContextResponse
data class AccountTokenPrincipalContextResponse(val accountId: String, val type: AccountType) :
    TokenPrincipalContextResponse

data class ActorTokenPrincipalContextResponse(val actorId: String, val roleId: String) : TokenPrincipalContextResponse