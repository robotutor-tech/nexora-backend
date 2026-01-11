package com.robotutor.nexora.module.automation.interfaces.controller.mapper.config

import com.robotutor.nexora.module.automation.domain.entity.config.Config
import com.robotutor.nexora.module.automation.interfaces.controller.dto.config.request.ConfigRequest
import com.robotutor.nexora.module.automation.interfaces.controller.dto.config.response.ConfigResponse

interface Mapper<C : Config, R : ConfigResponse, CR : ConfigRequest> {
    fun toConfigResponse(config: C): R
    fun toConfig(request: CR): C
}