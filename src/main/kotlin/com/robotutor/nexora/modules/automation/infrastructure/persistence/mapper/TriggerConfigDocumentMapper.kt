package com.robotutor.nexora.modules.automation.infrastructure.persistence.mapper

import com.robotutor.nexora.modules.automation.domain.entity.config.*
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.ScheduleTriggerConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.SunTriggerConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.TimeTriggerConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.VoiceTriggerConfigDocument

object TriggerConfigDocumentMapper {
    fun toScheduleTriggerConfigDocument(config: ScheduleTriggerConfig): ScheduleTriggerConfigDocument {
        val scheduleConfigDocument = when (config.config) {
            is SunConfig -> toSunTriggerConfigDocument(config.config)
            is TimeConfig -> toTimeTriggerConfigDocument(config.config)
        }
        return ScheduleTriggerConfigDocument(config = scheduleConfigDocument, repeat = config.repeat)
    }

    fun toVoiceTriggerConfigDocument(config: VoiceConfig): VoiceTriggerConfigDocument {
        return VoiceTriggerConfigDocument(config.commands.commands.map { it.command })
    }

    private fun toTimeTriggerConfigDocument(config: TimeConfig): TimeTriggerConfigDocument {
        return TimeTriggerConfigDocument(time = "${config.time.hour}:${config.time.minute}")
    }

    private fun toSunTriggerConfigDocument(config: SunConfig): SunTriggerConfigDocument {
        return SunTriggerConfigDocument(event = config.event, offsetMinutes = config.offsetMinutes)
    }

    fun toScheduleTriggerConfig(config: ScheduleTriggerConfigDocument): ScheduleTriggerConfig {
        val scheduleConfig = when (config.config) {
            is SunTriggerConfigDocument -> toSunTriggerConfig(config.config)
            is TimeTriggerConfigDocument -> toTimeTriggerConfig(config.config)
        }
        return ScheduleTriggerConfig(
            config = scheduleConfig,
            repeat = config.repeat
        )
    }

    private fun toTimeTriggerConfig(config: TimeTriggerConfigDocument): TimeConfig {
        val hourAndMinute = config.time.split(":")
        return TimeConfig(
            time = Time(hour = hourAndMinute[0].toInt(), minute = hourAndMinute[1].toInt())
        )
    }

    private fun toSunTriggerConfig(config: SunTriggerConfigDocument): SunConfig {
        return SunConfig(event = config.event, offsetMinutes = config.offsetMinutes)
    }

    fun toVoiceTriggerConfig(config: VoiceTriggerConfigDocument): VoiceConfig {
        return VoiceConfig(VoiceCommands(config.commands.map { VoiceCommand(it) }))
    }
}