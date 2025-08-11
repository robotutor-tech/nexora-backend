package com.robotutor.nexora.modules.automation.models

import com.robotutor.nexora.modules.premises.models.PremisesId
import org.bson.types.ObjectId
import java.time.Instant

data class Automation(
    var id: ObjectId? = null,
    val automationId: AutomationId,
    val premisesId: PremisesId,
    val name: String,
    val description: String? = null,
    val triggers: List<TriggerId>,
    var condition: ConditionNode? = null,
    val actions: List<ActionId>,
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

sealed interface ConditionNode {
    val type: ConditionNodeType
}

data class ConditionLeaf(
    val conditionId: ConditionId,
    override val type: ConditionNodeType = ConditionNodeType.LEAF
) : ConditionNode

data class ConditionGroup(
    val operator: LogicalOperator,
    val children: List<ConditionNode>,
    override val type: ConditionNodeType = ConditionNodeType.GROUP
) : ConditionNode

data class ConditionNot(
    val child: ConditionNode,
    override val type: ConditionNodeType = ConditionNodeType.NOT
) : ConditionNode

enum class LogicalOperator {
    AND, OR
}

enum class ConditionNodeType {
    LEAF,
    GROUP,
    NOT
}

typealias AutomationId = String
