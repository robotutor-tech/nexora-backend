package com.robotutor.nexora.module.automation.interfaces.controller.view.component.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.robotutor.nexora.module.automation.domain.vo.component.ComponentType

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = AutomationComponentRequest::class, name = "AUTOMATION"),
    JsonSubTypes.Type(value = FeedControlRequest::class, name = "FEED_CONTROL"),
    JsonSubTypes.Type(value = FeedValueRequest::class, name = "FEED_VALUE"),
    JsonSubTypes.Type(value = VoiceRequest::class, name = "VOICE"),
    JsonSubTypes.Type(value = WaitRequest::class, name = "WAIT"),
)
sealed class ComponentRequest(val type: ComponentType)