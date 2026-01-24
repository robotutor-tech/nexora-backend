package com.robotutor.nexora.module.automation.interfaces.controller.view

import com.robotutor.nexora.module.automation.domain.aggregate.AutomationState
import com.robotutor.nexora.module.automation.domain.aggregate.ExecutionMode
import com.robotutor.nexora.module.automation.interfaces.controller.view.component.response.ComponentResponse
import java.time.Instant


data class AutomationResponse(
    val automationId: String,
    val premisesId: String,
    val name: String,
    val triggers: List<ComponentResponse>,
    val condition: Any?,
    val actions: List<ComponentResponse>,
    val state: AutomationState,
    val executionMode: ExecutionMode,
    val createdOn: Instant,
    val expiresOn: Instant,
)
