package com.robotutor.nexora.module.automation.domain.entity

import com.robotutor.nexora.module.automation.domain.aggregate.AutomationState
import com.robotutor.nexora.module.automation.domain.aggregate.ExecutionMode
import com.robotutor.nexora.module.automation.domain.vo.AutomationId
import com.robotutor.nexora.module.automation.domain.vo.component.Action
import com.robotutor.nexora.module.automation.domain.vo.component.Condition
import com.robotutor.nexora.module.automation.domain.vo.component.Trigger
import com.robotutor.nexora.module.automation.domain.vo.component.data.ComponentData
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import java.time.Instant

data class ResolvedAutomation(
    val automationId: AutomationId,
    val premisesId: PremisesId,
    val name: Name,
    val description: String? = null,
    val triggers: List<ComponentData<Trigger>>,
    val condition: Specification<ComponentData<Condition>>? = null,
    val actions: List<ComponentData<Action>>,
    val state: AutomationState = AutomationState.ACTIVE,
    val executionMode: ExecutionMode = ExecutionMode.MULTIPLE,
    val createdOn: Instant = Instant.now(),
    val expiresOn: Instant = Instant.parse("9999-12-31T00:00:00.00Z"),
    val updatedOn: Instant = Instant.now(),
)