package com.robotutor.nexora.context.iam.interfaces.controller.view

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.robotutor.nexora.shared.domain.vo.AccountType

data class TokenResponses(val token: String, val refreshToken: String)
data class SessionValidateResponse(
    val isValid: Boolean,
    val principal: SessionPrincipalResponse,
    val expiresIn: Number,
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = AccountPrincipalResponse::class, name = "ACCOUNT"),
    JsonSubTypes.Type(value = ActorPrincipalResponse::class, name = "ACTOR"),
)
sealed interface SessionPrincipalResponse
data class AccountPrincipalResponse(val accountId: String, val accountType: AccountType) :
    SessionPrincipalResponse

data class ActorPrincipalResponse(
    val actorId: String,
    val premisesId: String,
    val accountId: String,
    val accountType: AccountType
) : SessionPrincipalResponse