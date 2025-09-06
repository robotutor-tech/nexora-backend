package com.robotutor.nexora.modules.automation.infrastructure.persistence.mapper

import com.robotutor.nexora.modules.automation.domain.entity.Action
import com.robotutor.nexora.modules.automation.domain.entity.ActionId
import com.robotutor.nexora.modules.automation.domain.entity.config.ActionConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.AutomationConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.FeedValueConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.WaitConfig
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.ActionDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.ActionConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.AutomationConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.FeedValueConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.WaitConfigDocument
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper
import org.springframework.stereotype.Component

@Component
class ActionDocumentMapper : DocumentMapper<Action, ActionDocument> {
    override fun toMongoDocument(domain: Action): ActionDocument {
        return ActionDocument(
            actionId = domain.actionId.value,
            premisesId = domain.premisesId.value,
            name = domain.name.value,
            description = domain.description,
            config = toConfigDocument(domain.config),
            createdOn = domain.createdOn,
            updatedOn = domain.updatedOn,
            version = domain.version,
        )
    }

    override fun toDomainModel(document: ActionDocument): Action {
        return Action(
            actionId = ActionId(document.actionId),
            premisesId = PremisesId(document.premisesId),
            name = Name(document.name),
            description = document.description,
            config = toConfigDomain(document.config),
            createdOn = document.createdOn,
            updatedOn = document.updatedOn,
            version = document.version

        )
    }

    fun toConfigDocument(config: ActionConfig): ActionConfigDocument {
        return when (config) {
            is AutomationConfig -> ActionConfigDocumentMapper.toAutomationConfigDocument(config)
            is FeedValueConfig -> ActionConfigDocumentMapper.toFeedValueConfigDocument(config)
            is WaitConfig -> ActionConfigDocumentMapper.toWaitConfigDocument(config)
        }
    }

    fun toConfigDomain(config: ActionConfigDocument): ActionConfig {
        return when (config) {
            is AutomationConfigDocument -> ActionConfigDocumentMapper.toAutomationConfig(config)
            is FeedValueConfigDocument -> ActionConfigDocumentMapper.toFeedValueConfig(config)
            is WaitConfigDocument -> ActionConfigDocumentMapper.toWaitConfig(config)
        }
    }
}