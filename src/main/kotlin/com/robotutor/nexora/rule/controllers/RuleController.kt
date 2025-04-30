package com.robotutor.nexora.rule.controllers

import com.robotutor.nexora.rule.services.RuleService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rules")
class RuleController(private val ruleService: RuleService) {
}