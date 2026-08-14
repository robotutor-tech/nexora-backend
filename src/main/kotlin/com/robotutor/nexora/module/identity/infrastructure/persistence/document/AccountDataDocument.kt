package com.robotutor.nexora.module.identity.infrastructure.persistence.document

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.robotutor.nexora.shared.domain.vo.principal.SubjectType

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = UserDataDocument::class, name = "USER"),
    JsonSubTypes.Type(value = DeviceDataDocument::class, name = "DEVICE"),
    JsonSubTypes.Type(value = ActorDataDocument::class, name = "ACTOR")
)
sealed interface AccountDataDocument {
    val accountId: String
    val subjectType: SubjectType
    val subjectId: String
}

data class UserDataDocument(
    val userId: String,
    override val accountId: String,
) : AccountDataDocument {
    override val subjectId: String = userId
    override val subjectType: SubjectType = SubjectType.USER
}

data class DeviceDataDocument(
    val deviceId: String,
    override val accountId: String,
) : AccountDataDocument {
    override val subjectId: String = deviceId
    override val subjectType: SubjectType = SubjectType.DEVICE
}


data class ActorDataDocument(
    val actorId: String,
    val premisesId: String,
    override val accountId: String,
    override val subjectType: SubjectType,
    override val subjectId: String
) : AccountDataDocument


