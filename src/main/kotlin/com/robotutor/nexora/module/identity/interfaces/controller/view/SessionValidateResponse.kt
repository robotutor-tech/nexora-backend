package com.robotutor.nexora.module.identity.interfaces.controller.view

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.robotutor.nexora.shared.domain.vo.SubjectType

data class SessionValidateResponse(
    val isValid: Boolean,
    val principal: AccountDataResponse,
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
sealed interface AccountDataResponse {
    val accountId: String
    val type: SubjectType
    val principalId: String
}

data class AccountPrincipalResponse(
    override val accountId: String,
    override val type: SubjectType,
    override val principalId: String
) : AccountDataResponse

data class ActorPrincipalResponse(
    val actorId: String,
    val premisesId: String,
    override val accountId: String,
    override val type: SubjectType,
    override val principalId: String
) : AccountDataResponse

