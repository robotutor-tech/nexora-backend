package com.robotutor.nexora.modules.automation.interfaces.controller.dto

import com.robotutor.nexora.modules.automation.domain.entity.objects.LogicalOperator

sealed class ConditionNodeResponse(val type: String)

data class ConditionLeafResponse(
    val conditionId: String,
) : ConditionNodeResponse("LEAF")

data class ConditionGroupResponse(
    val operator: LogicalOperator,
    val children: List<ConditionNodeResponse>,
) : ConditionNodeResponse("GROUP")

data class ConditionNotResponse(
    val child: ConditionNodeResponse,
) : ConditionNodeResponse("NOT")


