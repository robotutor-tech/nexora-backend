package com.robotutor.nexora.modules.automation.infrastructure.persistence.mapper

import com.robotutor.nexora.modules.automation.domain.entity.AutomationId
import com.robotutor.nexora.modules.automation.domain.entity.config.ActionConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.AutomationConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.FeedValueConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.WaitConfig
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.AutomationConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.FeedValueConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.WaitConfigDocument
import com.robotutor.nexora.shared.domain.model.FeedId
import org.springframework.stereotype.Component

object ActionConfigDocumentMapper {
    fun toAutomationConfigDocument(config: AutomationConfig): AutomationConfigDocument {
        return AutomationConfigDocument(config.automationId.value)
    }

    fun toFeedValueConfigDocument(config: FeedValueConfig): FeedValueConfigDocument {
        return FeedValueConfigDocument(config.feedId.value, config.value)
    }

    fun toWaitConfigDocument(config: WaitConfig): WaitConfigDocument {
        return WaitConfigDocument(config.duration)
    }

    fun toAutomationConfig(config: AutomationConfigDocument): AutomationConfig {
        return AutomationConfig(automationId = AutomationId(config.automationId))
    }

    fun toFeedValueConfig(config: FeedValueConfigDocument): FeedValueConfig {
        return FeedValueConfig(feedId = FeedId(config.feedId), value = config.value)
    }

    fun toWaitConfig(config: WaitConfigDocument): WaitConfig {
        return WaitConfig(duration = config.duration)
    }
}