package com.robotutor.nexora.shared.infrastructure.messaging.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app.events")
data class EventTopicProperties(val topics: Map<String, String>)
