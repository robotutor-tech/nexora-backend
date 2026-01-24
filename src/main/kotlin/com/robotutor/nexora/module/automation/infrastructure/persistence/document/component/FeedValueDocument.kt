package com.robotutor.nexora.module.automation.infrastructure.persistence.document.component

import com.robotutor.nexora.module.automation.domain.vo.component.ComponentType
import org.springframework.data.annotation.TypeAlias

@TypeAlias("FEED_VALUE")
data class FeedValueDocument(val feedId: String, val value: Int) : ComponentDocument(ComponentType.FEED_VALUE)
