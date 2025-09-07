package com.robotutor.nexora.modules.automation.domain.entity

import com.robotutor.nexora.modules.automation.application.command.CreateRuleCommand
import com.robotutor.nexora.modules.automation.domain.entity.config.ActionConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.ConditionConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.Config
import com.robotutor.nexora.modules.automation.domain.event.AutomationEvent
import com.robotutor.nexora.shared.domain.event.DomainAggregate
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.SequenceId
import com.robotutor.nexora.shared.domain.validation
import java.time.Instant

data class Rule(
    val ruleId: RuleId,
    val premisesId: PremisesId,
    val name: Name,
    val description: String? = null,
    val type: RuleType,
    val config: Config,
    val createdOn: Instant = Instant.now(),
    val updatedOn: Instant = Instant.now(),
    val version: Long? = null,
) : DomainAggregate<AutomationEvent>() {
    init {
        val isConfigValid = when (type) {
            RuleType.ACTION -> config is ActionConfig
            RuleType.CONDITION -> config is ConditionConfig
            RuleType.TRIGGER -> config is ConditionConfig
        }
        validation(isConfigValid) { "Invalid config for rule unit" }
    }

    companion object {
        fun create(ruleId: RuleId, createRuleCommand: CreateRuleCommand, actorData: ActorData): Rule {
            return Rule(
                ruleId = ruleId,
                premisesId = actorData.premisesId,
                name = createRuleCommand.name,
                description = createRuleCommand.description,
                type = createRuleCommand.type,
                config = createRuleCommand.config,
            )
        }
    }
}

data class RuleId(override val value: String) : SequenceId

enum class RuleType {
    ACTION,
    CONDITION,
    TRIGGER
}

