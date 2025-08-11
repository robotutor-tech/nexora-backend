package com.robotutor.nexora.modules.automation.controllers.views

import com.robotutor.nexora.modules.automation.models.Action
import com.robotutor.nexora.modules.automation.models.ActionConfig
import com.robotutor.nexora.modules.automation.models.ActionId
import com.robotutor.nexora.modules.premises.models.PremisesId
import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class ActionRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    val description: String? = null,
    val config: Map<String, Any?>
)

data class ActionView(
    val actionId: ActionId,
    val premisesId: PremisesId,
    val name: String,
    val description: String?,
    val config: ActionConfig,
    val createdOn: Instant,
    val updatedOn: Instant,
) {
    companion object {
        fun from(action: Action): ActionView {
            return ActionView(
                actionId = action.actionId,
                premisesId = action.premisesId,
                name = action.name,
                description = action.description,
                config = action.config,
                createdOn = action.createdOn,
                updatedOn = action.updatedOn,
            )
        }
    }
}
