package com.robotutor.nexora.module.automation.infrastructure.persistence.mapper.config

import com.robotutor.nexora.module.automation.domain.vo.component.VoiceConfig
import com.robotutor.nexora.module.automation.domain.entity.config.VoiceCommand
import com.robotutor.nexora.module.automation.domain.entity.config.VoiceCommands
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.config.VoiceConfigDocument

object VoiceDocumentMapper: Mapper<VoiceConfig, VoiceConfigDocument> {
    override fun toDocument(config: VoiceConfig): VoiceConfigDocument {
        return VoiceConfigDocument(commands = config.commands.commands.map { it.command })
    }

    override fun toDomain(doc: VoiceConfigDocument): VoiceConfig {
        return VoiceConfig(commands = VoiceCommands(doc.commands.map { VoiceCommand(it) }))
    }
}

