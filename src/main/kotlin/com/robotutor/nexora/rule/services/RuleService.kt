package com.robotutor.nexora.rule.services

import com.robotutor.nexora.rule.repositories.RuleRepository
import com.robotutor.nexora.security.services.IdGeneratorService
import org.springframework.stereotype.Service

@Service
class RuleService (private val idGeneratorService: IdGeneratorService, private val ruleRepository: RuleRepository) {

}
