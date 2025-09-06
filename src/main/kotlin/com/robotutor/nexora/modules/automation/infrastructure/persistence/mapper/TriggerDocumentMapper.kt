package com.robotutor.nexora.modules.automation.infrastructure.persistence.mapper

import com.robotutor.nexora.modules.automation.domain.entity.Trigger
import com.robotutor.nexora.modules.automation.domain.entity.TriggerId
import com.robotutor.nexora.modules.automation.domain.entity.config.FeedControlConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.ScheduleTriggerConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.TriggerConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.VoiceConfig
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.TriggerDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.FeedControlConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.ScheduleTriggerConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.TriggerConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.VoiceTriggerConfigDocument
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper
import org.springframework.stereotype.Component

@Component
class TriggerDocumentMapper : DocumentMapper<Trigger, TriggerDocument> {
    override fun toMongoDocument(domain: Trigger): TriggerDocument {
        return TriggerDocument(
            triggerId = domain.triggerId.value,
            premisesId = domain.premisesId.value,
            name = domain.name.value,
            description = domain.description,
            config = toConfigDocument(domain.config),
            createdOn = domain.createdOn,
            updatedOn = domain.updatedOn,
            version = domain.version,
        )
    }

    override fun toDomainModel(document: TriggerDocument): Trigger {
        return Trigger(
            triggerId = TriggerId(document.triggerId),
            premisesId = PremisesId(document.premisesId),
            name = Name(document.name),
            description = document.description,
            config = toConfigDomain(document.config),
            createdOn = document.createdOn,
            updatedOn = document.updatedOn,
            version = document.version
        )
    }

    fun toConfigDocument(config: TriggerConfig): TriggerConfigDocument {
        return when (config) {
            is FeedControlConfig -> ConfigMapper.toFeedControlConfigDocument(config)
            is ScheduleTriggerConfig -> TriggerConfigDocumentMapper.toScheduleTriggerConfigDocument(config)
            is VoiceConfig -> TriggerConfigDocumentMapper.toVoiceTriggerConfigDocument(config)
        }
    }

    fun toConfigDomain(config: TriggerConfigDocument): TriggerConfig {
        return when (config) {
            is FeedControlConfigDocument -> ConfigMapper.toFeedControlConfig(config)
            is ScheduleTriggerConfigDocument -> TriggerConfigDocumentMapper.toScheduleTriggerConfig(config)
            is VoiceTriggerConfigDocument -> TriggerConfigDocumentMapper.toVoiceTriggerConfig(config)
        }
    }
}