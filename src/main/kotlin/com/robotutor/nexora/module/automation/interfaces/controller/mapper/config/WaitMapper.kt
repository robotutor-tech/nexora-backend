package com.robotutor.nexora.module.automation.interfaces.controller.mapper.config

import com.robotutor.nexora.module.automation.domain.vo.component.Wait
import com.robotutor.nexora.module.automation.interfaces.controller.dto.config.request.WaitConfigRequest
import com.robotutor.nexora.module.automation.interfaces.controller.dto.config.response.WaitConfigResponse

object WaitMapper : Mapper<Wait, WaitConfigResponse, WaitConfigRequest> {
    override fun toConfigResponse(config: Wait): WaitConfigResponse {
        return WaitConfigResponse(duration = config.duration)
    }

    override fun toConfig(request: WaitConfigRequest): Wait {
        return Wait(duration = request.duration)
    }
}

