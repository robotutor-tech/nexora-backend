package com.robotutor.nexora.module.automation.infrastructure.persistence.document.component

import com.robotutor.nexora.module.automation.domain.vo.component.ComparisonOperator
import com.robotutor.nexora.module.automation.domain.vo.component.ComponentType
import org.springframework.data.annotation.TypeAlias

@TypeAlias("FEED_CONTROL")
data class FeedControlDocument(
    val feedId: String,
    val operator: ComparisonOperator,
    val value: Int
) : ComponentDocument(ComponentType.FEED_CONTROL)
