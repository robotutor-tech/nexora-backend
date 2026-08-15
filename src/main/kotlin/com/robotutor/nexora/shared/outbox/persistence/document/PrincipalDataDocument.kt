package com.robotutor.nexora.shared.outbox.persistence.document

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.AccountType
import com.robotutor.nexora.shared.domain.vo.PrincipalType

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "principalType"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = AccountDataDocument::class, name = "ACCOUNT"),
    JsonSubTypes.Type(value = ActorDataDocument::class, name = "ACTOR")
)
sealed interface PrincipalDataDocument {
    val principalId: String
    val principalType: PrincipalType
}

data class ActorDataDocument(
    val actorId: String,
    val premisesId: String,
    val accountData: AccountDataDocument,
) : PrincipalDataDocument {
    override val principalId: String = actorId
    override val principalType: PrincipalType = PrincipalType.ACTOR
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "accountType"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = DeviceDataDocument::class, name = "DEVICE"),
    JsonSubTypes.Type(value = UserDataDocument::class, name = "USER")
)
sealed class AccountDataDocument : PrincipalDataDocument {
    abstract val accountId: String
    abstract val accountType: AccountType
    override val principalType: PrincipalType = PrincipalType.ACCOUNT
}


data class UserDataDocument(
    override val principalId: String,
    override val accountId: String,
) : AccountDataDocument() {
    override val accountType: AccountType = AccountType.USER
}

data class DeviceDataDocument(
    override val principalId: String,
    override val accountId: String,
) : AccountDataDocument() {
    override val accountType: AccountType = AccountType.DEVICE
}



