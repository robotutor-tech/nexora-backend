package com.robotutor.nexora.automation.controllers.views

import com.robotutor.nexora.automation.models.*
import com.robotutor.nexora.premises.models.PremisesId
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import java.time.Instant

data class TriggerRequest(
    @field:NotEmpty(message = "At least one trigger is required")
    val triggers: List<TriggerId>,
    @field:NotEmpty(message = "At least one action is required")
    val actions: List<ActionId>,
    val condition: ConditionNode? = null,
    val executionMode: ExecutionMode? = null,
    @field:NotBlank(message = "Name is required")
    val name: String
)

data class TriggerView(
    val triggerId: TriggerId,
    val premisesId: PremisesId,
    val name: String,
    val description: String?,
    val type: TriggerType,
    val config: TriggerConfig,
    val createdOn: Instant,
    val updatedOn: Instant,
) {
    companion object {
        fun from(trigger: Trigger): TriggerView {
            return TriggerView(
                triggerId = trigger.triggerId,
                premisesId = trigger.premisesId,
                name = trigger.name,
                description = trigger.description,
                type = trigger.type,
                config = trigger.config,
                createdOn = trigger.createdOn,
                updatedOn = trigger.updatedOn,
            )
        }
    }
}
