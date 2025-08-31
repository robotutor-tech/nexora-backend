package com.robotutor.nexora.shared.infrastructure.jackson

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.robotutor.nexora.shared.domain.model.UserData
import com.robotutor.nexora.shared.domain.model.DeviceData

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = UserData::class, name = "user"),
    JsonSubTypes.Type(value = DeviceData::class, name = "device")
    // Add other subtypes as needed
)
interface ActorPrincipalDataMixin
