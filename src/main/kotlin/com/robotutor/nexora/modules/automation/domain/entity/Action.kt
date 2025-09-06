package com.robotutor.nexora.modules.automation.domain.entity

import com.robotutor.nexora.modules.automation.application.command.CreateActionCommand
import com.robotutor.nexora.modules.automation.domain.entity.config.ActionConfig
import com.robotutor.nexora.modules.automation.domain.event.AutomationEvent
import com.robotutor.nexora.shared.domain.event.DomainAggregate
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.SequenceId
import java.time.Instant

data class Action(
    val actionId: ActionId,
    val premisesId: PremisesId,
    val name: Name,
    val description: String? = null,
    val config: ActionConfig,
    val createdOn: Instant = Instant.now(),
    val updatedOn: Instant = Instant.now(),
    val version: Long? = null
) : DomainAggregate<AutomationEvent>() {
    companion object {
        fun create(actionId: ActionId, createActionCommand: CreateActionCommand, actorData: ActorData): Action {
            return Action(
                actionId = actionId,
                premisesId = actorData.premisesId,
                name = createActionCommand.name,
                description = createActionCommand.description,
                config = createActionCommand.config,
            )
        }
    }
}

data class ActionId(override val value: String) : SequenceId
