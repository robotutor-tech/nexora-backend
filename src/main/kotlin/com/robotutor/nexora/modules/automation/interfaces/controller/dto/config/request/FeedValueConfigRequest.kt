package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.request

import com.robotutor.nexora.modules.automation.domain.entity.config.ConfigType

data class FeedValueConfigRequest(val feedId: String, val value: Int) : ConfigRequest(ConfigType.FEED_VALUE)
