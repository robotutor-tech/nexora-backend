package com.robotutor.nexora.automation.controllers.views

import com.robotutor.nexora.automation.models.Trigger
import com.robotutor.nexora.automation.models.TriggerConfig
import com.robotutor.nexora.automation.models.TriggerId
import com.robotutor.nexora.automation.models.TriggerType
import com.robotutor.nexora.premises.models.PremisesId
import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class TriggerRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    val description: String? = null,
    val type: TriggerType,
    val config: TriggerConfig
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
