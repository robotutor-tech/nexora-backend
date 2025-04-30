package com.robotutor.nexora.rule.repositories

import com.robotutor.nexora.rule.models.Rule
import com.robotutor.nexora.rule.models.RuleId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RuleRepository : ReactiveCrudRepository<Rule, RuleId> {

}
