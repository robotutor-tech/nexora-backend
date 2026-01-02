package com.robotutor.nexora.module.iam.interfaces.controller.view

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.robotutor.nexora.shared.domain.vo.principal.AccountType

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
sealed interface SessionPrincipalResponse {
    val accountId: String
    val type: AccountType
    val principalId: String
}

data class AccountPrincipalResponse(
    override val accountId: String,
    override val type: AccountType,
    override val principalId: String
) : SessionPrincipalResponse

data class ActorPrincipalResponse(
    val actorId: String,
    val premisesId: String,
    override val accountId: String,
    override val type: AccountType,
    override val principalId: String
) : SessionPrincipalResponse

