package com.robotutor.nexora.modules.automation.domain.entity

import com.robotutor.nexora.modules.automation.domain.entity.objects.ConditionNode
import com.robotutor.nexora.shared.domain.vo.Identifier
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ValueObject
import java.time.Instant

data class Automation(
    val automationId: AutomationId,
    val premisesId: PremisesId,
    val name: Name,
    val description: String? = null,
    val triggers: Rules,
    val condition: ConditionNode?,
    val actions: Rules,
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

data class Rules(val ruleIds: List<RuleId>) {
    init {
        require(ruleIds.isNotEmpty()) { "Automation must have at least one rule" }
    }
}

data class Conditions(val conditionNode: ConditionNode?)

data class AutomationId(override val value: String) : Identifier, ValueObject()
