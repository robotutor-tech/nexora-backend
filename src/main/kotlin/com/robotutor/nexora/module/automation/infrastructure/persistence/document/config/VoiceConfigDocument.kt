package com.robotutor.nexora.module.automation.infrastructure.persistence.document.config

data class VoiceConfigDocument(val commands: List<String>) : ConfigDocument(ConfigType.VOICE)
