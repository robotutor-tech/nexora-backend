package com.robotutor.nexora.modules.automation.interfaces.controller

import com.robotutor.nexora.modules.automation.application.RuleUseCase
import com.robotutor.nexora.modules.automation.domain.entity.RuleId
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.RuleRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.RuleResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.mapper.RuleMapper
import com.robotutor.nexora.shared.application.annotation.RequireAccess
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.domain.model.ResourcesData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/rules")
class RuleController(private val ruleUseCase: RuleUseCase) {

    @RequireAccess(ActionType.WRITE, ResourceType.AUTOMATION_RULE)
    @PostMapping
    fun createTrigger(@RequestBody @Validated request: RuleRequest, actorData: ActorData): Mono<RuleResponse> {
        val command = RuleMapper.toCreateRuleCommand(request)
        return ruleUseCase.createRule(command, actorData)
            .map { RuleMapper.toRuleResponse(it) }
    }

    @RequireAccess(ActionType.READ, ResourceType.AUTOMATION_RULE)
    @GetMapping
    fun getRules(actorData: ActorData, resourcesData: ResourcesData): Flux<RuleResponse> {
        val ruleIds = resourcesData.getResourceIds(ActionType.READ, ResourceType.AUTOMATION_RULE)
            .map { RuleId(it) }
        return ruleUseCase.getRules(ruleIds, actorData)
            .map { RuleMapper.toRuleResponse(it) }
    }

    @RequireAccess(ActionType.READ, ResourceType.AUTOMATION_RULE, "ruleId")
    @GetMapping("/{ruleId}")
    fun getRule(@PathVariable ruleId: String, actorData: ActorData): Mono<RuleResponse> {
        return ruleUseCase.getRule(RuleId(ruleId), actorData)
            .map { RuleMapper.toRuleResponse(it) }
    }
}

