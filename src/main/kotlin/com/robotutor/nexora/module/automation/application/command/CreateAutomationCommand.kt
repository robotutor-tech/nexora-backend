package com.robotutor.nexora.module.automation.application.command

import com.robotutor.nexora.module.automation.domain.aggregate.ExecutionMode
import com.robotutor.nexora.module.automation.domain.vo.Actions
import com.robotutor.nexora.module.automation.domain.vo.Triggers
import com.robotutor.nexora.module.automation.domain.vo.component.Condition
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class CreateAutomationCommand(
    val triggers: Triggers,
    val actions: Actions,
    val condition: Specification<Condition>?,
    val executionMode: ExecutionMode,
    val name: Name,
    val description: String?,
    val createdBy: ActorId,
    val premisesId: PremisesId
)
