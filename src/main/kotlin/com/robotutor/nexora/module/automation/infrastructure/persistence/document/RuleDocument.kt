package com.robotutor.nexora.module.automation.infrastructure.persistence.document

import com.robotutor.nexora.common.persistence.document.MongoDocument
import com.robotutor.nexora.module.automation.domain.entity.Rule
import com.robotutor.nexora.module.automation.domain.entity.RuleType
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.config.ConfigDocument
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val RULE_COLLECTION = "rules"

@TypeAlias("Rule")
@CompoundIndexes(
    CompoundIndex(
        name = "unique_rule_by_premisesId_type_config",
        def = "{'premisesId': 1, 'type': 1, 'config': 1}",
        unique = true
    )
)
@Document(RULE_COLLECTION)
data class RuleDocument(
    val id: String? = null,
    @Indexed(unique = true)
    val ruleId: String,
    @Indexed
    val premisesId: String,
    val name: String,
    val description: String?,
    val type: RuleType,
    val config: ConfigDocument,
    val createdOn: Instant,
    val updatedOn: Instant,
    @Version
    val version: Long? = null,
) : MongoDocument<Rule>

