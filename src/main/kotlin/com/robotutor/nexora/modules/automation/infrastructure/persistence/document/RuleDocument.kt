package com.robotutor.nexora.modules.automation.infrastructure.persistence.document

import com.robotutor.nexora.modules.automation.domain.entity.Rule
import com.robotutor.nexora.modules.automation.domain.entity.RuleType
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.ConfigDocument
import com.robotutor.nexora.shared.infrastructure.persistence.model.MongoDocument
import org.bson.types.ObjectId
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
    var id: ObjectId? = null,
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
    val version: Long?,
) : MongoDocument<Rule>

