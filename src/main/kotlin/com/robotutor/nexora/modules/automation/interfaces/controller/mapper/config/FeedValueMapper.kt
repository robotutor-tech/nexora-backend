package com.robotutor.nexora.modules.automation.interfaces.controller.mapper.config

import com.robotutor.nexora.modules.automation.domain.entity.config.FeedValueConfig
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.request.FeedValueConfigRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.response.FeedValueConfigResponse
import com.robotutor.nexora.shared.domain.vo.FeedId

object FeedValueMapper : Mapper<FeedValueConfig, FeedValueConfigResponse, FeedValueConfigRequest> {
    override fun toConfigResponse(config: FeedValueConfig): FeedValueConfigResponse {
        return FeedValueConfigResponse(
            feedId = config.feedId.value,
            value = config.value
        )
    }

    override fun toConfig(request: FeedValueConfigRequest): FeedValueConfig {
        return FeedValueConfig(
            feedId = FeedId(request.feedId),
            value = request.value
        )
    }
}

