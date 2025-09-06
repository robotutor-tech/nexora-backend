package com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config

import com.robotutor.nexora.modules.automation.domain.entity.config.ComparisonOperator

sealed interface ConfigDocument
sealed interface ActionConfigDocument : ConfigDocument
sealed interface ConditionConfigDocument : ConfigDocument
sealed interface TriggerConfigDocument : ConfigDocument

data class FeedControlConfigDocument(
    val feedId: String,
    val operator: ComparisonOperator,
    val value: Int
) : ConditionConfigDocument, TriggerConfigDocument
