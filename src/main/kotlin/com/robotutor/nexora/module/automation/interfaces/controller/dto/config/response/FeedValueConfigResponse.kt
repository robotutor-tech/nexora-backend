package com.robotutor.nexora.module.automation.interfaces.controller.dto.config.response

data class FeedValueConfigResponse(
    val feedId: String,
    val value: Int
) : ConfigResponse(ConfigType.FEED_VALUE)

