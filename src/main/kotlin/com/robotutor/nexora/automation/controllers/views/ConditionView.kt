package com.robotutor.nexora.automation.controllers.views

import com.robotutor.nexora.automation.models.Condition
import com.robotutor.nexora.automation.models.ConditionConfig
import com.robotutor.nexora.automation.models.ConditionId
import com.robotutor.nexora.automation.models.ConditionType
import com.robotutor.nexora.premises.models.PremisesId
import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class ConditionRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    val description: String? = null,
    val type: ConditionType,
    val config: ConditionConfig,
)

data class ConditionView(
    val conditionId: ConditionId,
    val premisesId: PremisesId,
    val name: String,
    val description: String?,
    val type: ConditionType,
    val config: ConditionConfig,
    val createdOn: Instant,
    val updatedOn: Instant,
) {
    companion object {
        fun from(condition: Condition): ConditionView {
            return ConditionView(
                conditionId = condition.conditionId,
                premisesId = condition.premisesId,
                name = condition.name,
                description = condition.description,
                type = condition.type,
                config = condition.config,
                createdOn = condition.createdOn,
                updatedOn = condition.updatedOn,
            )
        }
    }
}
