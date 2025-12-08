package com.robotutor.nexora.common.security.infrastructure.facade.view

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.robotutor.nexora.context.iam.domain.aggregate.AccountType
import com.robotutor.nexora.context.iam.domain.aggregate.TokenPrincipalType

data class TokenValidationResponse(
    val isValid: Boolean,
    val principalType: TokenPrincipalType,
    val principal: TokenPrincipalContextResponse,
    val expiresIn: Number,
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "contextType"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = AccountTokenPrincipalContextResponse::class, name = "account"),
    JsonSubTypes.Type(value = ActorTokenPrincipalContextResponse::class, name = "actor"),
    JsonSubTypes.Type(value = InternalTokenPrincipalContextResponse::class, name = "internal")
)
sealed interface TokenPrincipalContextResponse
data class AccountTokenPrincipalContextResponse(val accountId: String, val type: AccountType) :
    TokenPrincipalContextResponse

data class ActorTokenPrincipalContextResponse(val actorId: String, val roleId: String) : TokenPrincipalContextResponse
data class InternalTokenPrincipalContextResponse(val id: String) : TokenPrincipalContextResponse