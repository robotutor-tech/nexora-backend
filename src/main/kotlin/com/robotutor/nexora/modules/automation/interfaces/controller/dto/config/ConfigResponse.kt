package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config

sealed interface ConfigResponse

sealed interface ActionConfigResponse : ConfigResponse
sealed interface TriggerConfigResponse : ConfigResponse
sealed interface ConditionConfigResponse : ConfigResponse


data class FeedControlConfigResponse(
    val feedId: String,
    val value: Int
) : TriggerConfigResponse, ConditionConfigResponse
