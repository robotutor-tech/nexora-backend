package com.robotutor.nexora.modules.automation.infrastructure.persistence.document

import com.robotutor.nexora.modules.automation.domain.entity.Trigger
import com.robotutor.nexora.modules.automation.domain.entity.TriggerType
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.TriggerConfigDocument
import com.robotutor.nexora.shared.infrastructure.persistence.model.MongoDocument
import org.bson.types.ObjectId
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val TRIGGER_COLLECTION = "triggers"

@TypeAlias("Trigger")
@CompoundIndexes(
    CompoundIndex(
        name = "unique_trigger_by_premisesId_type_config",
        def = "{'premisesId': 1, 'type': 1, 'config': 1}",
        unique = true
    )
)
@Document(TRIGGER_COLLECTION)
data class TriggerDocument(
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val triggerId: String,
    @Indexed
    val premisesId: String,
    val name: String,
    val description: String?,
    val type: TriggerType,
    val config: TriggerConfigDocument,
    val createdOn: Instant,
    val updatedOn: Instant,
    @Version
    val version: Long?
) : MongoDocument<Trigger>

