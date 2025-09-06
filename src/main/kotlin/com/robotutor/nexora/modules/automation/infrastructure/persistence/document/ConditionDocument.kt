package com.robotutor.nexora.modules.automation.infrastructure.persistence.document

import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.ConditionConfigDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.ConfigDocument
import org.bson.types.ObjectId
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val CONDITION_COLLECTION = "conditions"

@TypeAlias("Condition")
@CompoundIndexes(
    CompoundIndex(
        name = "unique_condition_by_premisesId_type_config",
        def = "{'premisesId': 1, 'type': 1, 'config': 1}",
        unique = true
    )
)
@Document(CONDITION_COLLECTION)
data class ConditionDocument(
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val conditionId: String,
    @Indexed
    val premisesId: String,
    val name: String,
    val description: String?,
    val config: ConditionConfigDocument,
    val createdOn: Instant,
    val updatedOn: Instant,
    @Version
    val version: Long?
)