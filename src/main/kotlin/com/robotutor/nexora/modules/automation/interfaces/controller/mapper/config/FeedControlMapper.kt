package com.robotutor.nexora.modules.automation.interfaces.controller.mapper.config

import com.robotutor.nexora.modules.automation.domain.entity.config.FeedControlConfig
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.request.FeedControlConfigRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.response.FeedControlConfigResponse
import com.robotutor.nexora.shared.domain.vo.FeedId

object FeedControlMapper : Mapper<FeedControlConfig, FeedControlConfigResponse, FeedControlConfigRequest> {
    override fun toConfigResponse(config: FeedControlConfig): FeedControlConfigResponse {
        return FeedControlConfigResponse(
            feedId = config.feedId.value,
            value = config.value,
            operator = config.operator
        )
    }

    override fun toConfig(request: FeedControlConfigRequest): FeedControlConfig {
        return FeedControlConfig(
            feedId = FeedId(request.feedId),
            operator = request.operator,
            value = request.value
        )
    }
}