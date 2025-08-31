package com.robotutor.nexora.shared.infrastructure.jackson

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.robotutor.nexora.shared.domain.model.*

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = UserData::class, name = "user"),
    JsonSubTypes.Type(value = DeviceData::class, name = "device"),
    JsonSubTypes.Type(value = ActorData::class, name = "actor"),
    JsonSubTypes.Type(value = InternalData::class, name = "internal"),
    JsonSubTypes.Type(value = InvitationData::class, name = "invitation")
)
interface PrincipalDataMixin
