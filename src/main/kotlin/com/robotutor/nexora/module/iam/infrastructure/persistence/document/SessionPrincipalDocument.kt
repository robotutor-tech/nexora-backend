package com.robotutor.nexora.module.iam.infrastructure.persistence.document

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.robotutor.nexora.shared.domain.vo.principal.AccountType

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = AccountPrincipalDocument::class, name = "ACCOUNT"),
    JsonSubTypes.Type(value = ActorPrincipalDocument::class, name = "ACTOR")
)
sealed interface SessionPrincipalDocument {
    val accountId: String
    val type: AccountType
    val principalId: String
}

data class AccountPrincipalDocument(
    override val accountId: String,
    override val type: AccountType,
    override val principalId: String
) : SessionPrincipalDocument

data class ActorPrincipalDocument(
    val actorId: String,
    val premisesId: String,
    override val accountId: String,
    override val type: AccountType,
    override val principalId: String
) : SessionPrincipalDocument


