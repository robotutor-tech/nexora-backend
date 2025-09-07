package com.robotutor.nexora.modules.automation.interfaces.controller.mapper.config

import com.robotutor.nexora.modules.automation.domain.entity.config.VoiceConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.VoiceCommand
import com.robotutor.nexora.modules.automation.domain.entity.config.VoiceCommands
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.request.VoiceConfigRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.response.VoiceConfigResponse

object VoiceMapper : Mapper<VoiceConfig, VoiceConfigResponse, VoiceConfigRequest> {
    override fun toConfigResponse(config: VoiceConfig): VoiceConfigResponse {
        return VoiceConfigResponse(
            commands = config.commands.commands.map { it.command }
        )
    }

    override fun toConfig(request: VoiceConfigRequest): VoiceConfig {
        val commands = VoiceCommands(request.commands.map { VoiceCommand(it) })
        return VoiceConfig(commands = commands)
    }
}

