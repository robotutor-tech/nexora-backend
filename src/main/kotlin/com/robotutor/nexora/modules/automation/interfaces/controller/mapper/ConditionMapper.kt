package com.robotutor.nexora.modules.automation.interfaces.controller.mapper

import com.robotutor.nexora.modules.automation.application.command.CreateConditionCommand
import com.robotutor.nexora.modules.automation.domain.entity.Condition
import com.robotutor.nexora.modules.automation.domain.entity.config.ConditionConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.Time
import com.robotutor.nexora.modules.automation.domain.entity.config.TimeRangeConfig
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.ConditionRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.ConditionResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.ConditionConfigRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.TimeRangeConfigRequest
import com.robotutor.nexora.shared.domain.model.Name

object ConditionMapper {
    fun toCreateConditionCommand(request: ConditionRequest): CreateConditionCommand {
        return CreateConditionCommand(
            name = Name(request.name),
            description = request.description,
            type = request.type,
            config = toConditionConfig(request.config)
        )
    }

    fun toConditionResponse(condition: Condition): ConditionResponse {
        TODO("Not yet implemented")
    }

    private fun toConditionConfig(configRequest: ConditionConfigRequest): ConditionConfig {
        return when (configRequest) {
            is TimeRangeConfigRequest -> TimeRangeConfig(
                startTime = Time.from(configRequest.startTime),
                endTime = Time.from(configRequest.endTime)
            )
        }
    }

}
