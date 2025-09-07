package com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config

import com.robotutor.nexora.modules.automation.domain.entity.config.ConfigType

data class VoiceConfigDocument(val commands: List<String>) : ConfigDocument(ConfigType.VOICE)
