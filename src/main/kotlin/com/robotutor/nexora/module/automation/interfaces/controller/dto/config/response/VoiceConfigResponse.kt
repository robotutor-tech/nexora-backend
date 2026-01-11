package com.robotutor.nexora.module.automation.interfaces.controller.dto.config.response

data class VoiceConfigResponse(
    val commands: List<String>
) : ConfigResponse(ConfigType.VOICE)

