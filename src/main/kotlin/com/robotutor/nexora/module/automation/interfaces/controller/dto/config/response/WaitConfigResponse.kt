package com.robotutor.nexora.module.automation.interfaces.controller.dto.config.response

data class WaitConfigResponse(
    val duration: Int
) : ConfigResponse(ConfigType.WAIT)
