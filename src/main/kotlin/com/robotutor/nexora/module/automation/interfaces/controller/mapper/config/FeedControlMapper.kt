package com.robotutor.nexora.module.automation.interfaces.controller.mapper.config

import com.robotutor.nexora.module.automation.domain.vo.component.FeedControl
import com.robotutor.nexora.module.automation.interfaces.controller.dto.config.request.FeedControlConfigRequest
import com.robotutor.nexora.module.automation.interfaces.controller.dto.config.response.FeedControlConfigResponse
import com.robotutor.nexora.shared.domain.vo.FeedId

object FeedControlMapper : Mapper<FeedControl, FeedControlConfigResponse, FeedControlConfigRequest> {
    override fun toConfigResponse(config: FeedControl): FeedControlConfigResponse {
        return FeedControlConfigResponse(
            feedId = config.feedId.value,
            value = config.value,
            operator = config.operator
        )
    }

    override fun toConfig(request: FeedControlConfigRequest): FeedControl {
        return FeedControl(
            feedId = FeedId(request.feedId),
            operator = request.operator,
            value = request.value
        )
    }
}