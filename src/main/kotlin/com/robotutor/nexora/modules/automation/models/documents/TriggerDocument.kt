package com.robotutor.nexora.modules.automation.models.documents

import com.robotutor.nexora.modules.automation.controllers.views.TriggerRequest
import com.robotutor.nexora.modules.automation.models.TriggerConfig
import com.robotutor.nexora.modules.automation.models.TriggerId
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.models.PremisesActorData
import com.robotutor.nexora.utils.toMap
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
    val triggerId: TriggerId,
    @Indexed
    val premisesId: PremisesId,
    val name: String,
    val description: String? = null,
    val config: Map<String, Any?>,
    val createdOn: Instant = Instant.now(),
    val updatedOn: Instant = Instant.now(),
    @Version
    val version: Long? = null
) {
    companion object {
        fun from(
            triggerId: TriggerId,
            config: TriggerConfig,
            request: TriggerRequest,
            premisesActorData: PremisesActorData
        ): TriggerDocument {
            return TriggerDocument(
                triggerId = triggerId,
                premisesId = premisesActorData.premisesId,
                name = request.name,
                description = request.description,
                config = config.toMap(),
            )
        }
    }
}