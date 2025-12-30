package com.robotutor.nexora.modules.automation.interfaces.controller

import com.robotutor.nexora.common.security.domain.vo.AuthorizedResources
import com.robotutor.nexora.modules.automation.application.RuleUseCase
import com.robotutor.nexora.modules.automation.domain.entity.RuleId
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.RuleRequest
import com.robotutor.nexora.modules.automation.interfaces.controller.dto.RuleResponse
import com.robotutor.nexora.modules.automation.interfaces.controller.mapper.RuleMapper
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/rules")
class RuleController(private val ruleUseCase: RuleUseCase) {

    @PostMapping
    fun createTrigger(@RequestBody @Validated request: RuleRequest, actorData: ActorData): Mono<RuleResponse> {
        val command = RuleMapper.toCreateRuleCommand(request)
        return ruleUseCase.createRule(command, actorData)
            .map { RuleMapper.toRuleResponse(it) }
    }

    @GetMapping
    fun getRules(actorData: ActorData, authorizedResources: AuthorizedResources): Flux<RuleResponse> {
        val ruleIds = emptyList<RuleId>()
        return ruleUseCase.getRules(ruleIds, actorData)
            .map { RuleMapper.toRuleResponse(it) }
    }

    @GetMapping("/{ruleId}")
    fun getRule(@PathVariable ruleId: String, actorData: ActorData): Mono<RuleResponse> {
        return ruleUseCase.getRule(RuleId(ruleId), actorData)
            .map { RuleMapper.toRuleResponse(it) }
    }
}

