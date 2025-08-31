package com.robotutor.nexora.shared.infrastructure.serializer.jackson

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.robotutor.nexora.shared.domain.model.ActorContext
import com.robotutor.nexora.shared.domain.model.DeviceContext
import com.robotutor.nexora.shared.domain.model.InternalContext
import com.robotutor.nexora.shared.domain.model.InvitationContext
import com.robotutor.nexora.shared.domain.model.UserContext

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = UserContext::class, name = "user"),
    JsonSubTypes.Type(value = DeviceContext::class, name = "device"),
    JsonSubTypes.Type(value = InternalContext::class, name = "internal"),
    JsonSubTypes.Type(value = InvitationContext::class, name = "invitation"),
    JsonSubTypes.Type(value = ActorContext::class, name = "actor"),
)
interface PrincipalContextMixin