package com.robotutor.nexora.module.automation.infrastructure.persistence.document.config

import com.robotutor.nexora.module.automation.domain.vo.component.ComparisonOperator

data class FeedControlConfigDocument(
    val feedId: String,
    val operator: ComparisonOperator,
    val value: Int
) : ConfigDocument(ConfigType.FEED_CONTROL)
