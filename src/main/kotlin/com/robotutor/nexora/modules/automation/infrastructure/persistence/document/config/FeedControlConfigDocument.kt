package com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config

import com.robotutor.nexora.modules.automation.domain.entity.objects.ComparisonOperator

data class FeedControlConfigDocument(
    val feedId: String,
    val operator: ComparisonOperator,
    val value: Int
) : ConfigDocument
