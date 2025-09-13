package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.robotutor.nexora.modules.automation.domain.entity.config.ConfigType

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ScheduleTriggerConfigRequest::class, name = "SCHEDULE"),
    JsonSubTypes.Type(value = VoiceConfigRequest::class, name = "VOICE"),
    JsonSubTypes.Type(value = FeedControlConfigRequest::class, name = "FEED_CONTROL"),
    JsonSubTypes.Type(value = FeedValueConfigRequest::class, name = "FEED_VALUE"),
    JsonSubTypes.Type(value = WaitConfigRequest::class, name = "WAIT"),
    JsonSubTypes.Type(value = TimeRangeConfigRequest::class, name = "TIME_RANGE"),
)
sealed class ConfigRequest(val type: ConfigType)