package com.robotutor.nexora.module.automation.interfaces.controller.mapper.config

import com.robotutor.nexora.module.automation.domain.vo.component.FeedValue
import com.robotutor.nexora.module.automation.interfaces.controller.dto.config.request.FeedValueConfigRequest
import com.robotutor.nexora.module.automation.interfaces.controller.dto.config.response.FeedValueConfigResponse
import com.robotutor.nexora.shared.domain.vo.FeedId

object FeedValueMapper : Mapper<FeedValue, FeedValueConfigResponse, FeedValueConfigRequest> {
    override fun toConfigResponse(config: FeedValue): FeedValueConfigResponse {
        return FeedValueConfigResponse(
            feedId = config.feedId.value,
            value = config.value
        )
    }

    override fun toConfig(request: FeedValueConfigRequest): FeedValue {
        return FeedValue(
            feedId = FeedId(request.feedId),
            value = request.value
        )
    }
}

