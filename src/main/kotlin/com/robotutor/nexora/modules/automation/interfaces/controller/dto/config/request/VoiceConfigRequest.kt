package com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.request

import com.robotutor.nexora.modules.automation.domain.entity.config.ConfigType

data class VoiceConfigRequest(val commands: List<String>) : ConfigRequest(ConfigType.VOICE)
