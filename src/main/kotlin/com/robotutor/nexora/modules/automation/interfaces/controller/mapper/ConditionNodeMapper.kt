package com.robotutor.nexora.modules.automation.interfaces.controller.mapper

import com.robotutor.nexora.modules.automation.domain.entity.objects.ConditionGroup
import com.robotutor.nexora.modules.automation.domain.entity.objects.ConditionLeaf
import com.robotutor.nexora.modules.automation.domain.entity.objects.ConditionNode
import com.robotutor.nexora.modules.automation.domain.entity.objects.ConditionNot
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.ConditionGroupResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.ConditionLeafResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.ConditionNodeResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.ConditionNotResponse

object ConditionNodeMapper {

    fun toConditionNodeResponse(condition: ConditionNode): ConditionNodeResponse {
        return when (condition) {
            is ConditionGroup -> toConditionGroupResponse(condition)
            is ConditionLeaf -> toConditionLeafResponse(condition)
            is ConditionNot -> toConditionNotResponse(condition)
        }
    }

    private fun toConditionNotResponse(condition: ConditionNot): ConditionNotResponse {
        return ConditionNotResponse(
            child = toConditionNodeResponse(condition.child)
        )
    }

    private fun toConditionLeafResponse(condition: ConditionLeaf): ConditionLeafResponse {
        return ConditionLeafResponse(
            conditionId = condition.conditionId.value
        )
    }

    private fun toConditionGroupResponse(condition: ConditionGroup): ConditionGroupResponse {
        return ConditionGroupResponse(
            operator = condition.operator,
            children = condition.children.map { toConditionNodeResponse(it) }
        )
    }
}
