package com.robotutor.nexora.module.automation.domain.aggregate

import com.robotutor.nexora.module.automation.domain.event.AutomationEvent
import com.robotutor.nexora.module.automation.domain.event.AutomationRegisteredEvent
import com.robotutor.nexora.module.automation.domain.vo.Actions
import com.robotutor.nexora.module.automation.domain.vo.AutomationId
import com.robotutor.nexora.module.automation.domain.vo.Triggers
import com.robotutor.nexora.module.automation.domain.vo.component.Condition
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import java.time.Instant

data class AutomationAggregate(
    val automationId: AutomationId,
    val premisesId: PremisesId,
    val name: Name,
    val description: String? = null,
    val triggers: Triggers,
    val condition: Specification<Condition>? = null,
    val actions: Actions,
    val state: AutomationState = AutomationState.ACTIVE,
    val executionMode: ExecutionMode = ExecutionMode.MULTIPLE,
    val createdOn: Instant = Instant.now(),
    val expiresOn: Instant = Instant.parse("9999-12-31T00:00:00.00Z"),
    val updatedOn: Instant = Instant.now(),
) : AggregateRoot<AutomationAggregate, AutomationId, AutomationEvent>(automationId) {
    companion object {
        fun register(
            name: Name,
            premisesId: PremisesId,
            triggers: Triggers,
            actions: Actions,
            condition: Specification<Condition>? = null,
            executionMode: ExecutionMode = ExecutionMode.MULTIPLE
        ): AutomationAggregate {
            val automation = AutomationAggregate(
                automationId = AutomationId.generate(),
                premisesId = premisesId,
                name = name,
                triggers = triggers,
                actions = actions,
                condition = condition,
                executionMode = executionMode
            )
            automation.addEvent(AutomationRegisteredEvent(automation.automationId))
            return automation
        }
    }
}

enum class AutomationState {
    ACTIVE,
    INACTIVE,
}

enum class ExecutionMode {
    MULTIPLE,
    SINGLE,
    REPLACE
}
