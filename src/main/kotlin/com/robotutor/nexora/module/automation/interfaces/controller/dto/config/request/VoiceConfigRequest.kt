package com.robotutor.nexora.module.automation.interfaces.controller.dto.config.request

data class VoiceConfigRequest(val commands: List<String>) : ConfigRequest(ConfigType.VOICE)
