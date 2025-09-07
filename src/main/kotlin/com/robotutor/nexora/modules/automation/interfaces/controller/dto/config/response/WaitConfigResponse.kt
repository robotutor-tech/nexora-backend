package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.response

import com.robotutor.nexora.modules.automation.domain.entity.config.ConfigType

data class WaitConfigResponse(
    val duration: Int
) : ConfigResponse(ConfigType.WAIT)
