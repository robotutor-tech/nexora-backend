package com.robotutor.nexora.modules.automation.interfaces.controller.mapper

import com.robotutor.nexora.modules.automation.application.command.CreateActionCommand
import com.robotutor.nexora.modules.automation.domain.entity.*
import com.robotutor.nexora.modules.automation.domain.entity.config.ActionConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.AutomationConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.FeedValueConfig
import com.robotutor.nexora.modules.automation.domain.entity.config.WaitConfig
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.ActionConfigResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.ActionRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.ActionResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.AutomationConfigResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.FeedValueConfigResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.WaitConfigResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.ActionConfigRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.AutomationConfigRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.FeedValueConfigRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.config.WaitConfigRequest
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.model.Name

object ActionMapper {
    fun toCreateActionCommand(request: ActionRequest): CreateActionCommand {
        return CreateActionCommand(
            name = Name(request.name),
            description = request.description,
            config = toActionConfig(request.config)
        )
    }

    fun toActionResponse(action: Action): ActionResponse {
        return ActionResponse(
            actionId = action.actionId.value,
            premisesId = action.premisesId.value,
            name = action.name.value,
            config = toActionConfigResponse(action.config),
            description = action.description,
            createdOn = action.createdOn,
            updatedOn = action.updatedOn
        )
    }

    private fun toActionConfigResponse(config: ActionConfig): ActionConfigResponse {
        return when (config) {
            is AutomationConfig -> AutomationConfigResponse(automationId = config.automationId.value)
            is FeedValueConfig -> FeedValueConfigResponse(feedId = config.feedId.value, value = config.value)
            is WaitConfig -> WaitConfigResponse(duration = config.duration)
        }
    }

    fun toActionConfig(configRequest: ActionConfigRequest): ActionConfig {
        return when (configRequest) {
            is AutomationConfigRequest -> AutomationConfig(automationId = AutomationId(configRequest.automationId))
            is WaitConfigRequest -> WaitConfig(duration = configRequest.duration)
            is FeedValueConfigRequest -> FeedValueConfig(
                feedId = FeedId(configRequest.feedId),
                value = configRequest.value
            )
        }
    }

}