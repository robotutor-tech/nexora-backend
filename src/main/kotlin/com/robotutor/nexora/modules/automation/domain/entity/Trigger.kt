package com.robotutor.nexora.modules.automation.domain.entity

import com.robotutor.nexora.modules.automation.application.command.CreateTriggerCommand
import com.robotutor.nexora.modules.automation.domain.entity.config.TriggerConfig
import com.robotutor.nexora.modules.automation.domain.event.AutomationEvent
import com.robotutor.nexora.shared.domain.event.DomainAggregate
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.SequenceId
import java.time.Instant

data class Trigger(
    val triggerId: TriggerId,
    val premisesId: PremisesId,
    val name: Name,
    val description: String? = null,
    val type: TriggerType,
    val config: TriggerConfig,
    val createdOn: Instant = Instant.now(),
    val updatedOn: Instant = Instant.now(),
    val version: Long? = null,
) : DomainAggregate<AutomationEvent>() {
    companion object {
        fun create(triggerId: TriggerId, createTriggerCommand: CreateTriggerCommand, actorData: ActorData): Trigger {
            return Trigger(
                triggerId = triggerId,
                premisesId = actorData.premisesId,
                name = createTriggerCommand.name,
                description = createTriggerCommand.description,
                type = createTriggerCommand.type,
                config = createTriggerCommand.config,
            )
        }
    }
}

enum class TriggerType {
    SCHEDULE,
    VOICE,
    FEED_CONTROL
}

data class TriggerId(override val value: String) : SequenceId
