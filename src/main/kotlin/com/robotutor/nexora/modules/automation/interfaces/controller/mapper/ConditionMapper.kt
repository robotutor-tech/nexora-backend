package com.robotutor.nexora.modules.automation.interfaces.controller.mapper

import com.robotutor.nexora.modules.automation.application.command.CreateConditionCommand
import com.robotutor.nexora.modules.automation.domain.entity.Condition
import com.robotutor.nexora.modules.automation.domain.entity.config.ConditionConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.FeedControlConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.Time
import com.robotutor.nexora.modules.automation.domain.entity.config.TimeRangeConfig
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.ConditionRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.ConditionResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.ConditionConfigRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.ConditionConfigResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.TimeRangeConfigRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.TimeRangeConfigResponse
import com.robotutor.nexora.shared.domain.model.Name

object ConditionMapper {
    fun toCreateConditionCommand(request: ConditionRequest): CreateConditionCommand {
        return CreateConditionCommand(
            name = Name(request.name),
            description = request.description,
            config = toConditionConfig(request.config)
        )
    }

    fun toConditionResponse(condition: Condition): ConditionResponse {
        return ConditionResponse(
            conditionId = condition.conditionId.value,
            premisesId = condition.premisesId.value,
            name = condition.name.value,
            description = condition.description,
            config = toConditionConfigResponse(condition.config),
            createdOn = condition.createdOn,
            updatedOn = condition.updatedOn
        )
    }

    private fun toConditionConfigResponse(config: ConditionConfig): ConditionConfigResponse {
        return when (config) {
            is FeedControlConfig -> ConfigMapper.toFeedControlConfigResponse(config)
            is TimeRangeConfig -> TimeRangeConfigResponse(
                startTime = config.startTime.toTimeString(),
                endTime = config.endTime.toTimeString()
            )
        }
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
