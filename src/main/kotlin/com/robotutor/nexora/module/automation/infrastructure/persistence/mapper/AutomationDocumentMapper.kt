package com.robotutor.nexora.module.automation.infrastructure.persistence.mapper

import com.robotutor.nexora.common.persistence.mapper.DocumentMapper
import com.robotutor.nexora.module.automation.domain.aggregate.AutomationAggregate
import com.robotutor.nexora.module.automation.domain.vo.Actions
import com.robotutor.nexora.module.automation.domain.vo.AutomationId
import com.robotutor.nexora.module.automation.domain.vo.Triggers
import com.robotutor.nexora.module.automation.domain.vo.component.Action
import com.robotutor.nexora.module.automation.domain.vo.component.AutomationComponent
import com.robotutor.nexora.module.automation.domain.vo.component.Component
import com.robotutor.nexora.module.automation.domain.vo.component.FeedControl
import com.robotutor.nexora.module.automation.domain.vo.component.FeedValue
import com.robotutor.nexora.module.automation.domain.vo.component.Trigger
import com.robotutor.nexora.module.automation.domain.vo.component.Voice
import com.robotutor.nexora.module.automation.domain.vo.component.Wait
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.AutomationDocument
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.component.AutomationComponentDocument
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.component.ComponentDocument
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.component.FeedControlDocument
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.component.FeedValueDocument
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.component.VoiceDocument
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.component.WaitDocument
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import java.util.concurrent.TimeUnit

object AutomationDocumentMapper : DocumentMapper<AutomationAggregate, AutomationDocument> {
    override fun toMongoDocument(domain: AutomationAggregate): AutomationDocument {
        return AutomationDocument(
            id = domain.getObjectId(),
            automationId = domain.automationId.value,
            premisesId = domain.premisesId.value,
            name = domain.name.value,
            description = domain.description,
            triggers = domain.triggers.values.map { toComponentDocument(it) },
            condition = null,
            actions = domain.actions.values.map { toComponentDocument(it) },
            state = domain.state,
            executionMode = domain.executionMode,
            createdOn = domain.createdOn,
            expiresOn = domain.expiresOn,
            updatedOn = domain.updatedOn,
            version = domain.getVersion()
        )
    }

    override fun toDomainModel(document: AutomationDocument): AutomationAggregate {
        return AutomationAggregate.create(
            automationId = AutomationId(document.automationId),
            premisesId = PremisesId(document.premisesId),
            name = Name(document.name),
            triggers = Triggers(document.triggers.map { toComponent(it) as Trigger }),
            actions = Actions(document.actions.map { toComponent(it) as Action }),
            condition = null,
            executionMode = document.executionMode

        )
            .setObjectIdAndVersion(document.id, document.version)
    }

    private fun toComponentDocument(component: Component): ComponentDocument {
        return when (component) {
            is AutomationComponent -> AutomationComponentDocument(component.automationId.value)
            is FeedValue -> FeedValueDocument(component.feedId.value, component.value)
            is Wait -> WaitDocument(component.duration)
            is FeedControl -> FeedControlDocument(component.feedId.value, component.operator, component.value)
            is Voice -> VoiceDocument(component.commands)
        }
    }

    private fun toComponent(component: ComponentDocument): Component {
        return when (component) {
            is AutomationComponentDocument -> AutomationComponent(AutomationId(component.automationId))
            is FeedControlDocument -> FeedControl(FeedId(component.feedId), component.operator, component.value)
            is FeedValueDocument -> FeedValue(FeedId(component.feedId), component.value)
            is VoiceDocument -> Voice(component.commands)
            is WaitDocument -> Wait(component.duration, TimeUnit.SECONDS)
        }
    }

}

