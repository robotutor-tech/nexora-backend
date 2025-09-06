package com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config

data class AutomationConfigDocument(val automationId: String) : ActionConfigDocument
data class FeedValueConfigDocument(val feedId: String, val value: Int) : ActionConfigDocument
data class WaitConfigDocument(val duration: Int) : ActionConfigDocument
