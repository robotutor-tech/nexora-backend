package com.robotutor.nexora.modules.automation.interfaces.controller

import com.robotutor.nexora.modules.automation.application.RuleUseCase
import com.robotutor.nexora.modules.automation.domain.entity.RuleId
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.RuleRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.RuleResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.mapper.RuleMapper
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.interfaces.view.AuthorizedResources
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/rules")
class RuleController(private val ruleUseCase: RuleUseCase) {

    @Authorize(ActionType.UPDATE, ResourceType.AUTOMATION_RULE)
    @PostMapping
    fun createTrigger(@RequestBody @Validated request: RuleRequest, ActorData: ActorData): Mono<RuleResponse> {
        val command = RuleMapper.toCreateRuleCommand(request)
        return ruleUseCase.createRule(command, ActorData)
            .map { RuleMapper.toRuleResponse(it) }
    }

    @Authorize(ActionType.READ, ResourceType.AUTOMATION_RULE)
    @GetMapping
    fun getRules(ActorData: ActorData, authorizedResources: AuthorizedResources): Flux<RuleResponse> {
        val ruleIds = emptyList<RuleId>()
        return ruleUseCase.getRules(ruleIds, ActorData)
            .map { RuleMapper.toRuleResponse(it) }
    }

    @Authorize(ActionType.READ, ResourceType.AUTOMATION_RULE, "#ruleId")
    @GetMapping("/{ruleId}")
    fun getRule(@PathVariable ruleId: String, ActorData: ActorData): Mono<RuleResponse> {
        return ruleUseCase.getRule(RuleId(ruleId), ActorData)
            .map { RuleMapper.toRuleResponse(it) }
    }
}

