package com.robotutor.nexora.modules.automation.domain.entity.config

import com.robotutor.nexora.shared.domain.validation

data class VoiceConfig(val commands: VoiceCommands) : TriggerConfig , RuleConfigType(ConfigType.VOICE){
    init {
        validation(commands.commands.isNotEmpty()) { "Commands must not be empty" }
    }
}