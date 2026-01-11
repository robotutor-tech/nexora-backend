package com.robotutor.nexora.module.automation.infrastructure.persistence.document.config

data class FeedValueConfigDocument(val feedId: String, val value: Int) : ConfigDocument(ConfigType.FEED_VALUE)
