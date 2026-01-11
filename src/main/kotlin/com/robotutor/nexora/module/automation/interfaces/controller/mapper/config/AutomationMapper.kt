package com.robotutor.nexora.module.automation.interfaces.controller.mapper.config

import com.robotutor.nexora.module.automation.domain.entity.AutomationId
import com.robotutor.nexora.module.automation.interfaces.controller.dto.config.request.AutomationConfigRequest
import com.robotutor.nexora.module.automation.interfaces.controller.dto.config.response.AutomationConfigResponse

object AutomationMapper : Mapper<AutomationConfig, AutomationConfigResponse, AutomationConfigRequest> {
    override fun toConfigResponse(config: AutomationConfig): AutomationConfigResponse {
        return AutomationConfigResponse(
            automationId = config.automationId.value
        )
    }

    override fun toConfig(request: AutomationConfigRequest): AutomationConfig {
        return AutomationConfig(
            automationId = AutomationId(request.automationId)
        )
    }
}