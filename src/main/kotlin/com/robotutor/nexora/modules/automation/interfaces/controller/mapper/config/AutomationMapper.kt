package com.robotutor.nexora.modules.automation.interfaces.controller.mapper.config

import com.robotutor.nexora.modules.automation.domain.entity.AutomationId
import com.robotutor.nexora.modules.automation.domain.entity.config.AutomationConfig
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.request.AutomationConfigRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.response.AutomationConfigResponse

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