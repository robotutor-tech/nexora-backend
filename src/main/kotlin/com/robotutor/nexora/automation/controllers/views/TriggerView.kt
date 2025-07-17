package com.robotutor.nexora.automation.controllers.views

import com.robotutor.nexora.automation.models.*
import com.robotutor.nexora.premises.models.PremisesId
import jakarta.validation.constraints.NotBlank
import java.time.DayOfWeek
import java.time.Instant

data class TriggerRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    val description: String? = null,
    val config: Map<String, Any>,
)

data class ScheduleTriggerRequest(
    val type: TriggerType = TriggerType.SCHEDULE,
    val config: Map<String, Any?>,
    val repeat: List<DayOfWeek>
)


data class TriggerView(
    val triggerId: TriggerId,
    val premisesId: PremisesId,
    val name: String,
    val description: String?,
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
                config = trigger.config,
                createdOn = trigger.createdOn,
                updatedOn = trigger.updatedOn,
            )
        }
    }
}
