package com.robotutor.nexora.modules.automation.interfaces.controller.mapper

import com.robotutor.nexora.modules.automation.application.command.CreateTriggerCommand
import com.robotutor.nexora.modules.automation.domain.entity.Trigger
import com.robotutor.nexora.modules.automation.domain.entity.config.*
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.TriggerRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.TriggerResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.*
import com.robotutor.nexora.shared.domain.model.Name

object TriggerMapper {
    fun toCreateTriggerCommand(request: TriggerRequest): CreateTriggerCommand {
        return CreateTriggerCommand(
            name = Name(request.name),
            description = request.description,
            type = request.type,
            config = toTriggerConfig(request.config)
        )
    }

    fun toTriggerResponse(trigger: Trigger): TriggerResponse {
        return TriggerResponse(
            triggerId = trigger.triggerId.value,
            premisesId = trigger.premisesId.value,
            name = trigger.name.value,
            config = toTriggerConfigResponse(trigger.config),
            description = trigger.description,
            createdOn = trigger.createdOn,
            updatedOn = trigger.updatedOn
        )
    }

    private fun toTriggerConfigResponse(config: TriggerConfig): TriggerConfigResponse {
        return when (config) {
            is FeedControlConfig -> FeedControlConfigResponse(config.feedId.value, config.value)
            is ScheduleTriggerConfig -> toScheduleTriggerConfigResponse(config)
            is VoiceConfig -> VoiceConfigResponse(config.commands.commands.map { it.command })
        }
    }

    fun toTriggerConfig(configRequest: TriggerConfigRequest): TriggerConfig {
        return when (configRequest) {
            is ScheduleTriggerConfigRequest -> toScheduleTriggerConfig(configRequest)
            is VoiceConfigRequest -> VoiceConfig(
                commands = VoiceCommands(configRequest.commands.map { VoiceCommand(it) })
            )
        }
    }

    fun toScheduleTriggerConfig(configRequest: ScheduleTriggerConfigRequest): ScheduleTriggerConfig {
        val config = when (configRequest.config) {
            is SunConfigRequest -> SunConfig(configRequest.config.event, configRequest.config.offsetMinutes)
            is TimeConfigRequest -> TimeConfig(Time.from(configRequest.config.time))
        }
        return ScheduleTriggerConfig(config, configRequest.repeat)
    }


    private fun toScheduleTriggerConfigResponse(config: ScheduleTriggerConfig): TriggerConfigResponse {
        val response = when (config.config) {
            is SunConfig -> SunConfigResponse(event = config.config.event, offsetMinutes = config.config.offsetMinutes)
            is TimeConfig -> TimeConfigResponse(config.config.time.toTimeString())
        }
        return ScheduleTriggerConfigResponse(response, config.repeat)
    }

}