package com.robotutor.nexora.rule.models

import com.robotutor.nexora.premises.models.PremisesId
import org.bson.types.ObjectId
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

const val RULE_COLLECTION = "rules"

@TypeAlias("Rule")
@Document(RULE_COLLECTION)
data class Rule(
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val ruleId: RuleId,
    val premisesId: PremisesId,
    val name: String,
    val state: RuleState,
    @Version
    val version: Long? = null
)

enum class RuleState {
    ACTIVE,
    INACTIVE,
}

typealias RuleId = String
