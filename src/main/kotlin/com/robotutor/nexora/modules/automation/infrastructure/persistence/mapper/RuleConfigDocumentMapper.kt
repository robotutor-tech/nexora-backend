package com.robotutor.nexora.modules.automation.infrastructure.persistence.mapper

import com.robotutor.nexora.modules.automation.domain.entity.AutomationId
import com.robotutor.nexora.modules.automation.domain.entity.config.AutomationConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.FeedControlConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.FeedValueConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.Config
import com.robotutor.nexora.modules.automation.domain.entity.config.ScheduleConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.SunConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.Time
import com.robotutor.nexora.modules.automation.domain.entity.config.TimeConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.TimeRangeConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.VoiceCommand
import com.robotutor.nexora.modules.automation.domain.entity.config.VoiceCommands
import com.robotutor.nexora.modules.automation.domain.entity.config.VoiceConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.WaitConfig
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.AutomationConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.FeedControlConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.FeedValueConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.ConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.ScheduleConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.SunConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.TimeConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.TimeRangeConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.VoiceConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.WaitConfigDocument
import com.robotutor.nexora.shared.domain.model.FeedId

object ConfigDocumentMapper {

    fun toConfigDocument(config: Config): ConfigDocument {
        return when (config) {
            is FeedControlConfig -> toFeedControlConfigDocument(config)
            is AutomationConfig -> toAutomationConfigDocument(config)
            is FeedValueConfig -> toFeedValueConfigDocument(config)
            is WaitConfig -> toWaitConfigDocument(config)
            is ScheduleConfig -> toScheduleConfigDocument(config)
            is VoiceConfig -> toVoiceConfigDocument(config)
            is TimeRangeConfig -> toTimeRangeConfigDocument(config)
        }
    }

    fun toConfig(config: ConfigDocument): Config {
        return when (config) {
            is AutomationConfigDocument -> toAutomationConfig(config)
            is FeedControlConfigDocument -> toFeedControlConfig(config)
            is FeedValueConfigDocument -> toFeedValueConfig(config)
            is ScheduleConfigDocument -> toScheduleConfig(config)
            is VoiceConfigDocument -> toVoiceConfig(config)
            is WaitConfigDocument -> toWaitConfig(config)
            is TimeRangeConfigDocument -> toTimeRangeConfig(config)
        }
    }

    private fun toTimeRangeConfig(config: TimeRangeConfigDocument): TimeRangeConfig {
        return TimeRangeConfig(startTime = Time.from(config.start), endTime = Time.from(config.end))
    }

    private fun toFeedControlConfigDocument(config: FeedControlConfig): FeedControlConfigDocument {
        return FeedControlConfigDocument(feedId = config.feedId.value, operator = config.operator, value = config.value)
    }

    private fun toTimeRangeConfigDocument(config: TimeRangeConfig): TimeRangeConfigDocument {
        return TimeRangeConfigDocument(start = config.startTime.toTimeString(), end = config.endTime.toTimeString())
    }

    private fun toFeedControlConfig(config: FeedControlConfigDocument): FeedControlConfig {
        return FeedControlConfig(
            feedId = FeedId(config.feedId),
            operator = config.operator,
            value = config.value
        )
    }

    private fun toAutomationConfigDocument(config: AutomationConfig): AutomationConfigDocument {
        return AutomationConfigDocument(config.automationId.value)
    }

    private fun toFeedValueConfigDocument(config: FeedValueConfig): FeedValueConfigDocument {
        return FeedValueConfigDocument(config.feedId.value, config.value)
    }

    private fun toWaitConfigDocument(config: WaitConfig): WaitConfigDocument {
        return WaitConfigDocument(config.duration)
    }

    private fun toAutomationConfig(config: AutomationConfigDocument): AutomationConfig {
        return AutomationConfig(automationId = AutomationId(config.automationId))
    }

    private fun toFeedValueConfig(config: FeedValueConfigDocument): FeedValueConfig {
        return FeedValueConfig(feedId = FeedId(config.feedId), value = config.value)
    }

    private fun toWaitConfig(config: WaitConfigDocument): WaitConfig {
        return WaitConfig(duration = config.duration)
    }

    private fun toScheduleConfigDocument(config: ScheduleConfig): ScheduleConfigDocument {
        val scheduleConfigDocument = when (config.config) {
            is SunConfig -> toSunConfigDocument(config.config)
            is TimeConfig -> toTimeConfigDocument(config.config)
        }
        return ScheduleConfigDocument(config = scheduleConfigDocument, repeat = config.repeat)
    }

    private fun toVoiceConfigDocument(config: VoiceConfig): VoiceConfigDocument {
        return VoiceConfigDocument(config.commands.commands.map { it.command })
    }

    private fun toTimeConfigDocument(config: TimeConfig): TimeConfigDocument {
        return TimeConfigDocument(time = "${config.time.hour}:${config.time.minute}")
    }

    private fun toSunConfigDocument(config: SunConfig): SunConfigDocument {
        return SunConfigDocument(event = config.event, offsetMinutes = config.offsetMinutes)
    }

    private fun toScheduleConfig(config: ScheduleConfigDocument): ScheduleConfig {
        val scheduleConfig = when (config.config) {
            is SunConfigDocument -> toSunConfig(config.config)
            is TimeConfigDocument -> toTimeConfig(config.config)
        }
        return ScheduleConfig(
            config = scheduleConfig,
            repeat = config.repeat
        )
    }

    private fun toTimeConfig(config: TimeConfigDocument): TimeConfig {
        val hourAndMinute = config.time.split(":")
        return TimeConfig(
            time = Time(hour = hourAndMinute[0].toInt(), minute = hourAndMinute[1].toInt())
        )
    }

    private fun toSunConfig(config: SunConfigDocument): SunConfig {
        return SunConfig(event = config.event, offsetMinutes = config.offsetMinutes)
    }

    private fun toVoiceConfig(config: VoiceConfigDocument): VoiceConfig {
        return VoiceConfig(VoiceCommands(config.commands.map { VoiceCommand(it) }))
    }
}