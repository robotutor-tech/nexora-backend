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

class AutomationAggregate private constructor(
    val automationId: AutomationId,
    val premisesId: PremisesId,
    val name: Name,
    val description: String?,
    val triggers: Triggers,
    val condition: Specification<Condition>?,
    val actions: Actions,
    val state: AutomationState,
    val executionMode: ExecutionMode,
    val createdOn: Instant,
    val expiresOn: Instant,
    val updatedOn: Instant,
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
            val automation = create(
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

        fun create(
            automationId: AutomationId,
            premisesId: PremisesId,
            name: Name,
            triggers: Triggers,
            actions: Actions,
            condition: Specification<Condition>? = null,
            executionMode: ExecutionMode = ExecutionMode.MULTIPLE,
            description: String? = null,
            state: AutomationState = AutomationState.ACTIVE,
            createdOn: Instant = Instant.now(),
            updatedOn: Instant = Instant.now(),
            expiresOn: Instant = Instant.parse("9999-12-31T23:59:59.999Z"),
        ): AutomationAggregate {
            return AutomationAggregate(
                automationId = automationId,
                premisesId = premisesId,
                name = name,
                triggers = triggers,
                actions = actions,
                condition = condition,
                executionMode = executionMode,
                description = description,
                state = state,
                createdOn = createdOn,
                expiresOn = expiresOn,
                updatedOn = updatedOn
            )
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
