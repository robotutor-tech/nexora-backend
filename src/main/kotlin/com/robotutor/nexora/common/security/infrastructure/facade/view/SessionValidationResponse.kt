package com.robotutor.nexora.common.security.infrastructure.facade.view

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.robotutor.nexora.context.iam.domain.aggregate.AccountType

data class SessionValidationResponse(
    val isValid: Boolean,
    val principal: SessionPrincipalResponse,
    val expiresIn: Number,
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = AccountSessionPrincipalResponse::class, name = "ACCOUNT"),
    JsonSubTypes.Type(value = ActorSessionPrincipalResponse::class, name = "ACTOR"),
    JsonSubTypes.Type(value = InternalSessionPrincipalResponse::class, name = "INTERNAL")
)
sealed interface SessionPrincipalResponse
data class AccountSessionPrincipalResponse(val accountId: String, val accountType: AccountType) :
    SessionPrincipalResponse

data class ActorSessionPrincipalResponse(
    val actorId: String,
    val roleId: String,
    val accountId: String,
    val accountType: AccountType
) : SessionPrincipalResponse

data class InternalSessionPrincipalResponse(val id: String) : SessionPrincipalResponse