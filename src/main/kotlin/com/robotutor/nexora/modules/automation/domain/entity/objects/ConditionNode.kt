package com.robotutor.nexora.modules.automation.domain.entity.objects

import com.robotutor.nexora.modules.automation.domain.entity.ConditionId
import com.robotutor.nexora.shared.domain.validation

sealed interface ConditionNode

data class ConditionLeaf(
    val conditionId: ConditionId,
) : ConditionNode

data class ConditionGroup(
    val operator: LogicalOperator,
    val children: List<ConditionNode>,
) : ConditionNode {
    init {
        validation(children.distinct().size == children.size) { "Condition nodes cannot be duplicated" }
        validation(children.distinct().size >= 2) { "Condition nodes must contain at least 2 unique nodes" }
    }
}

data class ConditionNot(
    val child: ConditionNode,
) : ConditionNode

enum class LogicalOperator {
    AND, OR
}

enum class ConditionNodeType {
    LEAF,
    GROUP,
    NOT
}
