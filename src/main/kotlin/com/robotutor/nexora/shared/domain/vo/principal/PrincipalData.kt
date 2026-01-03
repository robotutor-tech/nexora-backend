package com.robotutor.nexora.shared.domain.vo.principal

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = InternalData::class, name = "INTERNAL"),
    JsonSubTypes.Type(value = ActorData::class, name = "ACTOR"),
    JsonSubTypes.Type(value = AccountData::class, name = "ACCOUNT")
)
sealed interface PrincipalData
