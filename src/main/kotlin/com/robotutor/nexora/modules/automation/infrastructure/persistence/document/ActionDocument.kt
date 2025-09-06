package com.robotutor.nexora.modules.automation.infrastructure.persistence.document

import com.robotutor.nexora.modules.automation.domain.entity.Action
import com.robotutor.nexora.modules.automation.domain.entity.ActionType
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.config.ActionConfigDocument
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

const val ACTION_COLLECTION = "actions"

@TypeAlias("Action")
@CompoundIndexes(
    CompoundIndex(
        name = "unique_action_by_premisesId_config",
        def = "{'premisesId': 1, 'config': 1}",
        unique = true
    )
)
@Document(ACTION_COLLECTION)
data class ActionDocument(
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val actionId: String,
    @Indexed
    val premisesId: String,
    val name: String,
    val description: String?,
    val type: ActionType,
    val config: ActionConfigDocument,
    val createdOn: Instant,
    val updatedOn: Instant,
    @Version
    val version: Long?
) : MongoDocument<Action>
