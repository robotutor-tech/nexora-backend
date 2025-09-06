package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config


data class FeedValueConfigResponse(
    val feedId: String,
    val value: Int
) : ActionConfigResponse


data class WaitConfigResponse(
    val duration: Int
) : ActionConfigResponse

data class AutomationConfigResponse(
    val automationId: String
) : ActionConfigResponse
