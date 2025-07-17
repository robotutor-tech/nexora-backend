package com.robotutor.nexora.automation.controllers.views

import com.robotutor.nexora.automation.models.*
import com.robotutor.nexora.premises.models.PremisesId
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import java.time.Instant

data class AutomationRequest(
    @field:NotEmpty(message = "At least one trigger is required")
    val triggers: List<TriggerId>,
    @field:NotEmpty(message = "At least one action is required")
    val actions: List<ActionId>,
    val condition: Map<String, Any?>? = null,
    val executionMode: ExecutionMode? = null,
    @field:NotBlank(message = "Name is required")
    val name: String,
    val description: String? = null,
) {
}

data class AutomationView(
    val automationId: AutomationId,
    val premisesId: PremisesId,
    val name: String,
    val triggers: List<TriggerId>,
    val condition: ConditionNode?,
    val actions: List<ActionId>,
    val state: AutomationState,
    val executionMode: ExecutionMode,
    val createdOn: Instant,
    val expiresOn: Instant,
) {
    companion object {
        fun from(automation: Automation): AutomationView {
            return AutomationView(
                automationId = automation.automationId,
                premisesId = automation.premisesId,
                name = automation.name,
                triggers = automation.triggers,
                condition = automation.condition,
                actions = automation.actions,
                state = automation.state,
                executionMode = automation.executionMode,
                createdOn = automation.createdOn,
                expiresOn = automation.expiresOn
            )
        }
    }
}
