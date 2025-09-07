package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.response

import com.robotutor.nexora.modules.automation.domain.entity.config.ConfigType

data class FeedValueConfigResponse(
    val feedId: String,
    val value: Int
) : ConfigResponse(ConfigType.FEED_VALUE)

