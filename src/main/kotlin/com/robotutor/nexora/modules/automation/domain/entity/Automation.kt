package com.robotutor.nexora.modules.automation.domain.entity

import com.robotutor.nexora.modules.automation.domain.entity.objects.ConditionNode
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import java.time.Instant

data class Automation(
    val automationId: AutomationId,
    val premisesId: PremisesId,
    val name: Name,
    val description: String? = null,
    val triggers: Triggers,
    val condition: ConditionNode?,
    val actions: Actions,
    val state: AutomationState = AutomationState.ACTIVE,
    val executionMode: ExecutionMode = ExecutionMode.MULTIPLE,
    val createdOn: Instant = Instant.now(),
    val expiresOn: Instant = Instant.parse("9999-12-31T00:00:00.00Z"),
    val updatedOn: Instant = Instant.now(),
    val version: Long? = null
)

enum class AutomationState {
    ACTIVE,
    INACTIVE,
}

enum class ExecutionMode {
    MULTIPLE,
    SINGLE,
    REPLACE
}

data class Triggers(val triggerIds: List<TriggerId>) {
    init {
        require(triggerIds.isNotEmpty()) { "Automation must have at least one trigger" }
    }
}

data class Conditions(val conditionNode: ConditionNode?)
data class Actions(val actionIds: List<ActionId>) {
    init {
        require(actionIds.isNotEmpty()) { "Automation must have at least one action" }
    }
}

data class AutomationId(val value: String)
