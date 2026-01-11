package com.robotutor.nexora.module.automation.interfaces.controller.dto.config.request

data class FeedValueConfigRequest(val feedId: String, val value: Int) : ConfigRequest(ConfigType.FEED_VALUE)
