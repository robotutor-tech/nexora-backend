package com.robotutor.nexora.module.automation.interfaces.controller.mapper

import com.robotutor.nexora.module.automation.domain.entity.Rule
import com.robotutor.nexora.module.automation.interfaces.controller.dto.RuleRequest
import com.robotutor.nexora.module.automation.interfaces.controller.dto.RuleResponse
import com.robotutor.nexora.module.automation.interfaces.controller.mapper.config.ConfigMapper
import com.robotutor.nexora.shared.domain.vo.Name

object RuleMapper {
    fun toCreateRuleCommand(request: RuleRequest): CreateRuleCommand {
        return CreateRuleCommand(
            name = Name(request.name),
            description = request.description,
            type = request.type,
            config = ConfigMapper.toConfig(request.config)
        )
    }

    fun toRuleResponse(rule: Rule): RuleResponse {
        return RuleResponse(
            ruleId = rule.ruleId.value,
            premisesId = rule.premisesId.value,
            name = rule.name.value,
            config = ConfigMapper.toConfigResponse(rule.config),
            type = rule.type,
            description = rule.description,
            createdOn = rule.createdOn,
            updatedOn = rule.updatedOn
        )
    }
}