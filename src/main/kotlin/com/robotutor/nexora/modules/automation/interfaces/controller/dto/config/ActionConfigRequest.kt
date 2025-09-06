package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config


data class AutomationConfigRequest(val automationId: String) : ActionConfigRequest
data class FeedValueConfigRequest(val feedId: String, val value: Int) : ActionConfigRequest
data class WaitConfigRequest(val duration: Int) : ActionConfigRequest
