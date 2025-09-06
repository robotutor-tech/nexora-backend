package com.robotutor.nexora.modules.automation.interfaces.controller.mapper

import com.robotutor.nexora.modules.automation.domain.entity.config.FeedControlConfig
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.FeedControlConfigResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.TriggerConfigResponse

object ConfigMapper {
    fun toFeedControlConfigResponse(config: FeedControlConfig): FeedControlConfigResponse {
        return FeedControlConfigResponse(
            feedId = config.feedId.value,
            value = config.value,
            operator = config.operator
        )
    }

}