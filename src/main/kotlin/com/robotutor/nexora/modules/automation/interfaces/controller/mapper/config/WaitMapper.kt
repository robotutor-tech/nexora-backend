package com.robotutor.nexora.modules.automation.interfaces.controller.mapper.config

import com.robotutor.nexora.modules.automation.domain.entity.config.WaitConfig
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.request.WaitConfigRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.response.WaitConfigResponse

object WaitMapper : Mapper<WaitConfig, WaitConfigResponse, WaitConfigRequest> {
    override fun toConfigResponse(config: WaitConfig): WaitConfigResponse {
        return WaitConfigResponse(duration = config.duration)
    }

    override fun toConfig(request: WaitConfigRequest): WaitConfig {
        return WaitConfig(duration = request.duration)
    }
}

